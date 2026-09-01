package orange.smart_hire.controller;

import orange.smart_hire.dto.InterviewResponse;
import orange.smart_hire.service.InterviewService;
import orange.smart_hire.utils.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/candidate")
@PreAuthorize("hasRole('CANDIDATE')")
public class CandidateController {

    private final InterviewService interviewService;

    public CandidateController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }


    @GetMapping("/my-interviews")
    public ResponseEntity<List<InterviewResponse>> getMyInterviews() {
        return ResponseEntity.ok(
                interviewService.getInterviewsByCandidate(SecurityUtils.getCurrentUserId())
        );
    }
}
