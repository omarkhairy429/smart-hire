package orange.smart_hire.controller;

import orange.smart_hire.dto.DossierResponse;
import orange.smart_hire.dto.InterviewResponse;
import orange.smart_hire.service.InterviewService;
import orange.smart_hire.utils.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/interviewer")
@PreAuthorize("hasRole('INTERVIEWER')")
public class InterviewerController {

    private final InterviewService interviewService;

    public InterviewerController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @GetMapping("/my-interviews")
    public ResponseEntity<List<InterviewResponse>> getMyInterviews() {
        return ResponseEntity.ok(
                interviewService.getMyInterviews(SecurityUtils.getCurrentUserId())
        );
    }

    @GetMapping("/interviews/{id}/dossier")
    public ResponseEntity<DossierResponse> getDossier(@PathVariable UUID id) {
        return ResponseEntity.ok(
                interviewService.getDossier(id, SecurityUtils.getCurrentUserId())
        );
    }
}
