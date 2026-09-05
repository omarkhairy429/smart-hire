package orange.smart_hire.controller;

import jakarta.validation.Valid;
import orange.smart_hire.dto.FeedbackResponse;
import orange.smart_hire.dto.InterviewResponse;
import orange.smart_hire.dto.ScheduleInterviewRequest;
import orange.smart_hire.dto.StaffResponse;
import orange.smart_hire.service.InterviewFeedbackService;
import orange.smart_hire.service.InterviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class InterviewController {

    private final InterviewService interviewService;
    private final InterviewFeedbackService feedbackService;

    public InterviewController(InterviewService interviewService,
                               InterviewFeedbackService feedbackService) {
        this.interviewService = interviewService;
        this.feedbackService = feedbackService;
    }

    @PostMapping("/api/applications/{applicationId}/interviews")
    @PreAuthorize("hasAnyRole('HR_MANAGER', 'SUPER_ADMIN')")
    public ResponseEntity<InterviewResponse> schedule(
            @PathVariable UUID applicationId,
            @Valid @RequestBody ScheduleInterviewRequest request
    ) {
        InterviewResponse response =
                interviewService.schedule(applicationId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/api/applications/{applicationId}/interviews")
    @PreAuthorize("hasAnyRole('HR_MANAGER', 'SUPER_ADMIN')")
    public ResponseEntity<List<InterviewResponse>> getByApplication(
            @PathVariable UUID applicationId
    ) {
        return ResponseEntity.ok(
                interviewService.getInterviewsByApplication(applicationId)
        );
    }

    @DeleteMapping("/api/interviews/{interviewId}")
    @PreAuthorize("hasAnyRole('HR_MANAGER', 'SUPER_ADMIN')")
    public ResponseEntity<Void> cancel(@PathVariable UUID interviewId) {
        interviewService.cancel(interviewId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/users/interviewers")
    @PreAuthorize("hasAnyRole('HR_MANAGER', 'SUPER_ADMIN')")
    public ResponseEntity<List<StaffResponse>> getInterviewers() {
        return ResponseEntity.ok(interviewService.getInterviewers());
    }

    @GetMapping("/api/interviews/{interviewId}/feedback")
    @PreAuthorize("hasAnyRole('HR_MANAGER', 'SUPER_ADMIN')")
    public ResponseEntity<List<FeedbackResponse>> getFeedbackForInterview(
            @PathVariable UUID interviewId
    ) {
        return ResponseEntity.ok(
                feedbackService.getFeedbackForInterview(interviewId)
        );
    }
}
