package orange.smart_hire.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class DossierResponse {

    private UUID interviewId;
    private LocalDateTime scheduledAt;
    private String meetingLink;

    private UUID candidateId;
    private String candidateName;
    private String candidateEmail;
    private String resumeUrl;
    private String coverLetter;
    private String experienceSummary;

    private UUID postingId;
    private String postingTitle;
    private String postingDescription;
    private List<String> skillsRequired;
}
