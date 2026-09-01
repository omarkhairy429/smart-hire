package orange.smart_hire.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ScheduleInterviewRequest {

    @NotNull
    private UUID interviewerId;

    @NotNull
    private LocalDateTime scheduledAt;

    @NotBlank
    private String meetingLink;
}
