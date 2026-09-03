package orange.smart_hire.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import orange.smart_hire.enums.FeedbackRecommendation;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "interview_feedbacks",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_feedback_interview_interviewer",
                columnNames = {"interview_id", "interviewer_id"}
        ))
@Getter
@Setter
public class InterviewFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "interview_id", nullable = false)
    private UUID interviewId;

    @Column(name = "interviewer_id", nullable = false)
    private UUID interviewerId;

    /** Overall rating 1–5 */
    @Column(columnDefinition = "INT CHECK (rating BETWEEN 1 AND 5)")
    private Integer rating;

    /** Technical skills score 1–5 */
    @Column(name = "technical_score")
    private Integer technicalScore;

    /** Communication skills score 1–5 */
    @Column(name = "communication_score")
    private Integer communicationScore;

    /** Recommendation: PROCEED, HOLD, or REJECT */
    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private FeedbackRecommendation recommendation;

    @Column(columnDefinition = "TEXT")
    private String comments;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
