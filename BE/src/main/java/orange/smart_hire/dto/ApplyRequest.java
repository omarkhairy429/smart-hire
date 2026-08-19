package orange.smart_hire.dto;

import lombok.Data;
import lombok.NonNull;


import java.util.UUID;

@Data
public class ApplyRequest {

    @NonNull
    private UUID postingId;
    private String coverLetter;
    private String experienceSummary;
    @NonNull
    private String resumeUrl;
}
