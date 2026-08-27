package orange.smart_hire.dto;

import lombok.Getter;
import lombok.Setter;
import orange.smart_hire.enums.ApplicationStage;

@Getter
@Setter
public class UpdateStageRequest {
    private ApplicationStage stage;
}
