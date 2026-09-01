package orange.smart_hire.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class InterviewResponse {

    private UUID id;
    private UUID applicationId;
    private UUID interviewerId;
    private LocalDateTime scheduledAt;
    private String meetingLink;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String interviewerName;
    private String candidateName;
    private String postingTitle;
}
