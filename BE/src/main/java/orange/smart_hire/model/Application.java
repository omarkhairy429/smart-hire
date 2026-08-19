package orange.smart_hire.model;

import jakarta.persistence.*;
import orange.smart_hire.enums.ApplicationStage;
import orange.smart_hire.enums.ApplicationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "applications",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_application_candidate_posting",
                        columnNames = {"candidate_id", "posting_id"}
                )
        })
public class Application {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "posting_id", nullable = false)
    private UUID postingId;

    @Column(name = "candidate_id", nullable = false)
    private UUID candidateId;

    private String coverLetter;

    private String experienceSummary;

    @Column(nullable = false)
    private String resumeUrl;

    @Enumerated(EnumType.STRING)
    private ApplicationStage stage;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
