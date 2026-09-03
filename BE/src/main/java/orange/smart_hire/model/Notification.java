package orange.smart_hire.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Getter
@Setter
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** The user who receives this notification */
    @Column(name = "recipient_id", nullable = false)
    private UUID recipientId;

    /** Notification type code e.g. INTERVIEW_SCHEDULED, FEEDBACK_SUBMITTED */
    @Column(nullable = false, length = 60)
    private String type;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    /** Optional: ID of the related entity (application, interview, etc.) */
    @Column(name = "related_entity_id")
    private UUID relatedEntityId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
