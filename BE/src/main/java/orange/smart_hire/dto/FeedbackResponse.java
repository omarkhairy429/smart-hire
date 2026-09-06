package orange.smart_hire.dto;

import lombok.Data;
import orange.smart_hire.enums.FeedbackRecommendation;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class FeedbackResponse {

    private UUID id;
    private UUID interviewId;
    private UUID interviewerId;
    private String interviewerName;

    private Integer rating;
    private Integer technicalScore;
    private Integer communicationScore;
    private FeedbackRecommendation recommendation;
    private String comments;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
