package lk.ciaboc.dashboard.auth;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "role_pin")
public class RolePin {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_pin_id")
    private Long id;
    @OneToOne
    @JoinColumn(name = "user_role_id", nullable = false, unique = true)
    private UserRole userRole;
    @Column(name = "pin_hash", nullable = false)
    private String pinHash;
    @Column(nullable = false)
    private String status = "ACTIVE";
    @Column(name = "failed_attempts", nullable = false)
    private int failedAttempts;
    @Column(name = "locked_until")
    private OffsetDateTime lockedUntil;
    @Column(name = "expires_at")
    private OffsetDateTime expiresAt;
    @Column(name = "created_at")
    private OffsetDateTime createdAt = OffsetDateTime.now();
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt = OffsetDateTime.now();
    @Column(name = "revoked_at")
    private OffsetDateTime revokedAt;

    public Long getId() { return id; }
    public UserRole getUserRole() { return userRole; }
    public void setUserRole(UserRole userRole) { this.userRole = userRole; }
    public String getPinHash() { return pinHash; }
    public void setPinHash(String pinHash) { this.pinHash = pinHash; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getFailedAttempts() { return failedAttempts; }
    public void setFailedAttempts(int failedAttempts) { this.failedAttempts = failedAttempts; }
    public OffsetDateTime getLockedUntil() { return lockedUntil; }
    public void setLockedUntil(OffsetDateTime lockedUntil) { this.lockedUntil = lockedUntil; }
    public OffsetDateTime getExpiresAt() { return expiresAt; }
    public OffsetDateTime getRevokedAt() { return revokedAt; }
    public void setRevokedAt(OffsetDateTime revokedAt) { this.revokedAt = revokedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setExpiresAt(OffsetDateTime expiresAt) { this.expiresAt = expiresAt; }
}
