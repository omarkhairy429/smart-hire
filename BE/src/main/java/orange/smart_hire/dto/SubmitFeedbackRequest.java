package orange.smart_hire.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import orange.smart_hire.enums.FeedbackRecommendation;

@Data
public class SubmitFeedbackRequest {

    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;

    @Min(1)
    @Max(5)
    private Integer technicalScore;

    @Min(1)
    @Max(5)
    private Integer communicationScore;

    @NotNull
    private FeedbackRecommendation recommendation;

    private String comments;
}
