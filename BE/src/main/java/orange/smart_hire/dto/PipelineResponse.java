package orange.smart_hire.dto;

import lombok.Getter;
import lombok.Setter;
import orange.smart_hire.enums.ApplicationStage;

import java.util.List;

@Getter
@Setter
public class PipelineResponse {
    private ApplicationStage stage;
    private List<ApplicationResponse> responses;


}
