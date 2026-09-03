package orange.smart_hire.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import orange.smart_hire.enums.InterviewFormat;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "interviews")
@Getter
@Setter
public class Interview {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "application_id", nullable = false)
    private UUID applicationId;

    @Column(name = "interviewer_id", nullable = false)
    private UUID interviewerId;

    @Column(name = "scheduled_at", nullable = false)
    private LocalDateTime scheduledAt;

    /** Format of the interview: IN_PERSON, VIDEO, or PHONE */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InterviewFormat format = InterviewFormat.VIDEO;

    /**
     * Physical location for IN_PERSON interviews.
     * Nullable — only required when format = IN_PERSON.
     */
    @Column(length = 512)
    private String location;

    /**
     * Meeting link for VIDEO / PHONE interviews.
     * Nullable — not required for IN_PERSON.
     */
    @Column(name = "meeting_link", length = 512)
    private String meetingLink;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
