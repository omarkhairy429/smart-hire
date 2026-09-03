package orange.smart_hire.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import orange.smart_hire.enums.InterviewFormat;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ScheduleInterviewRequest {

    @NotNull
    private UUID interviewerId;

    @NotNull
    private LocalDateTime scheduledAt;

    /** Format is required: IN_PERSON, VIDEO, or PHONE */
    @NotNull
    private InterviewFormat format;

    /**
     * Meeting link — required for VIDEO and PHONE formats.
     * Must be null or omitted for IN_PERSON.
     */
    private String meetingLink;

    /**
     * Physical location — used for IN_PERSON interviews.
     * Optional for other formats.
     */
    private String location;
}
