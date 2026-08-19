package orange.smart_hire.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;


import java.util.UUID;

@Data
public class ApplyRequest {

    @NotNull
    private UUID postingId;
    private String coverLetter;
    private String experienceSummary;
    @NotNull
    private String resumeUrl;
}
