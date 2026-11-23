package lk.ciaboc.dashboard.auth;

import jakarta.servlet.http.HttpSession;
import lk.ciaboc.dashboard.api.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    public record RoleView(Long userRoleId, String roleCode, String roleName, String organizationalUnit) {}
    public record SsoRoleRequest(String subject) {}
    public record RolePinRequest(Long userRoleId, String pin) {}
    public record ProvisionPinRequest(Long userRoleId) {}

    @GetMapping("/session")
    public ApiResponse<Object> session(HttpSession session) {
        String subject = (String) session.getAttribute(AuthService.SSO_SUBJECT_SESSION_KEY);
        if (subject == null) {
            return ApiResponse.success(Map.of("authenticated", false, "roles", List.of()));
        }
        List<RoleView> roles = auth.assignedRoles(subject).stream()
                .map(assignment -> new RoleView(
                        assignment.getId(),
                        assignment.getRole().getCode(),
                        assignment.getRole().getName(),
                        assignment.getOrganizationalUnit() == null ? null : assignment.getOrganizationalUnit().getCode()))
                .toList();
        Object selectedRole = session.getAttribute(AuthService.SELECTED_USER_ROLE_SESSION_KEY);
        return ApiResponse.success(Map.of(
                "authenticated", true,
                "roles", roles,
                "selectedUserRoleId", selectedRole == null ? null : selectedRole));
    }

    @PostMapping("/role/select")
    public ResponseEntity<ApiResponse<Object>> selectRole(@RequestBody RolePinRequest request,
                                                           HttpSession session) {
        String subject = (String) session.getAttribute(AuthService.SSO_SUBJECT_SESSION_KEY);
        if (subject == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("SSO authentication required"));
        }
        auth.findAuthorizedRole(subject, request.userRoleId())
                .orElseThrow(() -> new AuthService.UnauthorizedRoleException("Role is not assigned"));
        session.setAttribute("CIABOC_PENDING_USER_ROLE", request.userRoleId());
        auth.audit(subject, "ROLE_SELECTED", "SUCCESS", "USER_ROLE",
                String.valueOf(request.userRoleId()), "Role selected for PIN verification");
        return ResponseEntity.ok(ApiResponse.success(Map.of("requiresPin", true)));
    }

    @PostMapping("/role/verify")
    public ResponseEntity<ApiResponse<Object>> verifyRolePin(@RequestBody RolePinRequest request,
                                                              HttpSession session) {
        String subject = (String) session.getAttribute(AuthService.SSO_SUBJECT_SESSION_KEY);
        if (subject == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("SSO authentication required"));
        }
        auth.verifyPin(subject, request.userRoleId(), request.pin());
        Object pending = session.getAttribute("CIABOC_PENDING_USER_ROLE");
        if (!request.userRoleId().equals(pending)) {
            throw new AuthService.UnauthorizedRoleException("Role was not selected");
        }
        session.setAttribute(AuthService.SELECTED_USER_ROLE_SESSION_KEY, request.userRoleId());
        session.removeAttribute("CIABOC_PENDING_USER_ROLE");
        session.setMaxInactiveInterval(30 * 60);
        return ResponseEntity.ok(ApiResponse.success(Map.of("selectedRoleContext", true)));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpSession session) {
        auth.audit((String) session.getAttribute(AuthService.SSO_SUBJECT_SESSION_KEY),
                "ROLE_SESSION_LOGOUT", "SUCCESS", "SESSION", null, "Role session invalidated");
        session.invalidate();
        return ApiResponse.success(null);
    }

    @PostMapping("/admin/pins/provision")
    public ResponseEntity<ApiResponse<Map<String, Object>>> provisionPin(@RequestBody ProvisionPinRequest request,
                                                                          HttpSession session) {
        requireAdmin(session);
        return provision(request.userRoleId(), session);
    }

    @PostMapping("/admin/pins/reset")
    public ResponseEntity<ApiResponse<Map<String, Object>>> resetPin(@RequestBody ProvisionPinRequest request,
                                                                     HttpSession session) {
        requireAdmin(session);
        return provision(request.userRoleId(), session);
    }

    @PostMapping("/admin/pins/revoke")
    public ApiResponse<Void> revokePin(@RequestBody ProvisionPinRequest request, HttpSession session) {
        requireAdmin(session);
        Object selected = session.getAttribute(AuthService.SELECTED_USER_ROLE_SESSION_KEY);
        auth.findAssignment(request.userRoleId())
                .ifPresent(assignment -> auth.revokePin(assignment, (String) session.getAttribute(AuthService.SSO_SUBJECT_SESSION_KEY)));
        if (selected != null && selected.equals(request.userRoleId())) {
            session.removeAttribute(AuthService.SELECTED_USER_ROLE_SESSION_KEY);
        }
        return ApiResponse.success(null);
    }

    private ResponseEntity<ApiResponse<Map<String, Object>>> provision(Long userRoleId, HttpSession session) {
        UserRole assignment = auth.findAssignment(userRoleId)
                .orElseThrow(() -> new AuthService.UnauthorizedRoleException("Invalid user-role assignment"));
        String pin = auth.provisionPin(assignment);
        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "userRoleId", userRoleId,
                "pin", pin)));
    }

    private void requireAdmin(HttpSession session) {
        String subject = (String) session.getAttribute(AuthService.SSO_SUBJECT_SESSION_KEY);
        Long selected = (Long) session.getAttribute(AuthService.SELECTED_USER_ROLE_SESSION_KEY);
        if (subject == null || selected == null) {
            throw new AuthService.UnauthorizedRoleException("Authenticated role context required");
        }
        boolean admin = auth.findAuthorizedRole(subject, selected)
                .map(UserRole::getRole)
                .map(role -> "ICT_ADMIN".equals(role.getCode()))
                .orElse(false);
        if (!admin) {
            throw new AuthService.UnauthorizedRoleException("ICT_ADMIN role required");
        }
    }
}
