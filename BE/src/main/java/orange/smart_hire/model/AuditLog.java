package orange.smart_hire.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** The user who performed the action (nullable — system actions have no actor) */
    @Column(name = "actor_id")
    private UUID actorId;

    /** Action performed e.g. STAGE_UPDATED, STAFF_DEACTIVATED, STAFF_CREATED */
    @Column(nullable = false, length = 100)
    private String action;

    /** Type of entity affected e.g. Application, User */
    @Column(name = "entity_type", length = 60)
    private String entityType;

    /** ID of the affected entity */
    @Column(name = "entity_id")
    private UUID entityId;

    /** Arbitrary JSON payload with before/after or extra context */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> details;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
