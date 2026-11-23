package lk.ciaboc.dashboard.auth;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "audit_log")
public class AuditLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_log_id")
    private Long id;
    @Column(name = "occurred_at")
    private OffsetDateTime occurredAt = OffsetDateTime.now();
    @Column(name = "actor_subject")
    private String actorSubject;
    @Column(nullable = false)
    private String action;
    @Column(nullable = false)
    private String outcome;
    @Column(name = "target_type")
    private String targetType;
    @Column(name = "target_id")
    private String targetId;
    private String detail;

    public AuditLog() {}
    public AuditLog(String actorSubject, String action, String outcome, String targetType, String targetId, String detail) {
        this.actorSubject = actorSubject; this.action = action; this.outcome = outcome;
        this.targetType = targetType; this.targetId = targetId; this.detail = detail;
    }
}
