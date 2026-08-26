package orange.smart_hire.dto;

import lombok.Data;
import orange.smart_hire.enums.ApplicationStage;
import orange.smart_hire.enums.ApplicationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ApplicationResponse {
    private UUID id;

    private UUID postingId;


    private UUID candidateId;

    private String candidateName;

    private String candidateEmail;

    private String coverLetter;

    private String experienceSummary;

    private String resumeUrl;

    private ApplicationStage stage;

    private ApplicationStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
