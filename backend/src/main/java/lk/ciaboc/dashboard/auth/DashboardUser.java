package lk.ciaboc.dashboard.auth;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "dashboard_user")
public class DashboardUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    @Column(name = "external_subject", nullable = false, unique = true)
    private String externalSubject;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(name = "display_name", nullable = false)
    private String displayName;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organizational_unit_id")
    private OrganizationalUnit organizationalUnit;
    private boolean active = true;
    @Column(name = "created_at")
    private OffsetDateTime createdAt = OffsetDateTime.now();
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getExternalSubject() { return externalSubject; }
    public void setExternalSubject(String externalSubject) { this.externalSubject = externalSubject; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public OrganizationalUnit getOrganizationalUnit() { return organizationalUnit; }
    public void setOrganizationalUnit(OrganizationalUnit organizationalUnit) { this.organizationalUnit = organizationalUnit; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
