package lk.ciaboc.dashboard.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {
    public static final String SSO_SUBJECT_SESSION_KEY = "CIABOC_SSO_SUBJECT";
    public static final String SELECTED_USER_ROLE_SESSION_KEY = "CIABOC_SELECTED_USER_ROLE";

    private final DashboardUserRepository users;
    private final UserRoleRepository userRoles;
    private final RolePinRepository rolePins;
    private final DashboardRoleRepository roles;
    private final AuditLogRepository auditLogs;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final SecureRandom secureRandom = new SecureRandom();
    private final Map<String, Deque<Long>> verificationWindows = new ConcurrentHashMap<>();

    @Value("${dashboard.pin.max-attempts:5}")
    private int maxAttempts;
    @Value("${dashboard.pin.lock-duration-minutes:15}")
    private int lockDurationMinutes;
    @Value("${dashboard.pin.rate-limit-per-minute:10}")
    private int rateLimitPerMinute;

    public AuthService(DashboardUserRepository users, UserRoleRepository userRoles,
                       RolePinRepository rolePins, DashboardRoleRepository roles,
                       AuditLogRepository auditLogs) {
        this.users = users;
        this.userRoles = userRoles;
        this.rolePins = rolePins;
        this.roles = roles;
        this.auditLogs = auditLogs;
    }

    public Optional<DashboardUser> findBySubject(String subject) {
        return users.findByExternalSubject(subject);
    }

    public List<UserRole> assignedRoles(String subject) {
        return users.findByExternalSubject(subject)
                .filter(DashboardUser::isActive)
                .map(user -> userRoles.findByUser_IdAndActiveTrue(user.getId()))
                .orElseGet(List::of)
                .stream()
                .filter(UserRole::isActive)
                .filter(assignment -> assignment.getRole().isActive())
                .toList();
    }

    public Optional<UserRole> findAuthorizedRole(String subject, Long userRoleId) {
        return assignedRoles(subject).stream()
                .filter(assignment -> assignment.getId().equals(userRoleId))
                .findFirst();
    }

    public Optional<UserRole> findAssignment(Long userRoleId) {
        return userRoles.findById(userRoleId);
    }

    @Transactional
    public String provisionPin(UserRole userRole) {
        String pin = String.format("%08d", secureRandom.nextInt(100_000_000));
        RolePin credential = rolePins.findByUserRole_Id(userRole.getId()).orElse(new RolePin());
        credential.setUserRole(userRole);
        credential.setPinHash(passwordEncoder.encode(pin));
        credential.setStatus("ACTIVE");
        credential.setFailedAttempts(0);
        credential.setLockedUntil(null);
        credential.setRevokedAt(null);
        credential.setUpdatedAt(OffsetDateTime.now());
        rolePins.save(credential);
        auditLogs.save(new AuditLog(null, "ROLE_PIN_CREATED", "SUCCESS",
                "USER_ROLE", String.valueOf(userRole.getId()), "Role-specific PIN provisioned"));
        return pin;
    }

    @Transactional
    public void revokePin(UserRole userRole, String actorSubject) {
        rolePins.findByUserRole_Id(userRole.getId()).ifPresent(pin -> {
            pin.setStatus("REVOKED");
            pin.setRevokedAt(OffsetDateTime.now());
            pin.setUpdatedAt(OffsetDateTime.now());
            rolePins.save(pin);
        });
        auditLogs.save(new AuditLog(actorSubject, "ROLE_PIN_REVOKED", "SUCCESS",
                "USER_ROLE", String.valueOf(userRole.getId()), "Role access revoked"));
    }

    @Transactional
    public boolean verifyPin(String subject, Long userRoleId, String pin) {
        UserRole assignment = findAuthorizedRole(subject, userRoleId)
                .orElseThrow(() -> new UnauthorizedRoleException("Role is not assigned to this account"));
        String key = subject + ":" + userRoleId;
        if (!withinRateLimit(key)) {
            auditLogs.save(new AuditLog(subject, "ROLE_PIN_VERIFY", "DENIED",
                    "USER_ROLE", String.valueOf(userRoleId), "PIN verification rate limit exceeded"));
            throw new PinDeniedException("Too many PIN verification attempts");
        }

        RolePin credential = rolePins.findByUserRole_Id(userRoleId)
                .orElseThrow(() -> new PinDeniedException("PIN is not available"));
        OffsetDateTime now = OffsetDateTime.now();
        if (!"ACTIVE".equals(credential.getStatus())) {
            auditLogs.save(new AuditLog(subject, "ROLE_PIN_VERIFY", "DENIED",
                    "USER_ROLE", String.valueOf(userRoleId), "PIN is not active"));
            throw new PinDeniedException("PIN is not active");
        }
        if (credential.getLockedUntil() != null && credential.getLockedUntil().isAfter(now)) {
            auditLogs.save(new AuditLog(subject, "ROLE_PIN_VERIFY", "DENIED",
                    "USER_ROLE", String.valueOf(userRoleId), "PIN is temporarily locked"));
            throw new PinDeniedException("PIN is temporarily locked");
        }
        if (credential.getExpiresAt() != null && credential.getExpiresAt().isBefore(now)) {
            credential.setStatus("EXPIRED");
            rolePins.save(credential);
            throw new PinDeniedException("PIN has expired");
        }
        if (!pin.matches("\\d{8}") || !passwordEncoder.matches(pin, credential.getPinHash())) {
            int attempts = credential.getFailedAttempts() + 1;
            credential.setFailedAttempts(attempts);
            if (attempts >= maxAttempts) {
                credential.setLockedUntil(now.plusMinutes(lockDurationMinutes));
            }
            credential.setUpdatedAt(now);
            rolePins.save(credential);
            auditLogs.save(new AuditLog(subject, "ROLE_PIN_VERIFY", "FAILURE",
                    "USER_ROLE", String.valueOf(userRoleId), "Incorrect PIN"));
            throw new PinDeniedException("PIN verification failed");
        }

        credential.setFailedAttempts(0);
        credential.setLockedUntil(null);
        credential.setUpdatedAt(now);
        rolePins.save(credential);
        auditLogs.save(new AuditLog(subject, "ROLE_PIN_VERIFY", "SUCCESS",
                "USER_ROLE", String.valueOf(userRoleId), "Role PIN verified"));
        return true;
    }

    public void audit(String subject, String action, String outcome, String targetType,
                      String targetId, String detail) {
        auditLogs.save(new AuditLog(subject, action, outcome, targetType, targetId, detail));
    }

    public Optional<DashboardRole> findRoleByCode(String code) {
        return roles.findByCode(code);
    }

    private synchronized boolean withinRateLimit(String key) {
        long now = System.currentTimeMillis();
        Deque<Long> timestamps = verificationWindows.computeIfAbsent(key, ignored -> new ArrayDeque<>());
        while (!timestamps.isEmpty() && now - timestamps.peekFirst() > 60_000) {
            timestamps.removeFirst();
        }
        if (timestamps.size() >= rateLimitPerMinute) {
            return false;
        }
        timestamps.addLast(now);
        return true;
    }

    public static class UnauthorizedRoleException extends RuntimeException {
        public UnauthorizedRoleException(String message) { super(message); }
    }

    public static class PinDeniedException extends RuntimeException {
        public PinDeniedException(String message) { super(message); }
    }
}
