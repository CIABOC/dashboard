package lk.ciaboc.dashboard.auth;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "user_role", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "role_id"}))
public class UserRole {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_role_id")
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private DashboardUser user;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private DashboardRole role;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organizational_unit_id")
    private OrganizationalUnit organizationalUnit;
    private boolean active = true;
    @Column(name = "assigned_at")
    private OffsetDateTime assignedAt = OffsetDateTime.now();
    @Column(name = "revoked_at")
    private OffsetDateTime revokedAt;

    public Long getId() { return id; }
    public DashboardUser getUser() { return user; }
    public void setUser(DashboardUser user) { this.user = user; }
    public DashboardRole getRole() { return role; }
    public void setRole(DashboardRole role) { this.role = role; }
    public OrganizationalUnit getOrganizationalUnit() { return organizationalUnit; }
    public void setOrganizationalUnit(OrganizationalUnit organizationalUnit) { this.organizationalUnit = organizationalUnit; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public void setRevokedAt(OffsetDateTime revokedAt) { this.revokedAt = revokedAt; }
}
