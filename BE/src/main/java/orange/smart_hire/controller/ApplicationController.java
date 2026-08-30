package orange.smart_hire.controller;

import orange.smart_hire.dto.ApplicationResponse;
import orange.smart_hire.dto.ApplyRequest;
import orange.smart_hire.dto.UpdateStageRequest;
import orange.smart_hire.service.ApplicationService;
import orange.smart_hire.utils.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PreAuthorize("hasRole('CANDIDATE')")
    @PostMapping
    public ResponseEntity<ApplicationResponse> apply(
            @RequestBody ApplyRequest request,
            Authentication authentication
    ) {

        UUID candidateId =
                SecurityUtils.getCurrentUserId();

        ApplicationResponse response =
                applicationService.apply(request, candidateId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PreAuthorize("hasRole('CANDIDATE')")
    @PreAuthorize("hasRole('CANDIDATE')")
    @GetMapping
    public ResponseEntity<List<ApplicationResponse>> getMyApplications(
            Authentication authentication
    ) {

        UUID candidateId =
                SecurityUtils.getCurrentUserId();

        return ResponseEntity.ok(
                applicationService.getMyApplications(candidateId)
        );
    }
    @GetMapping("/posting/{postingId}")
    @PreAuthorize("hasAnyRole('HR_MANAGER', 'SUPER_ADMIN')")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsByPosting(
            @PathVariable UUID postingId
    ) {
        return ResponseEntity.ok(
                applicationService.getApplicationsByPosting(postingId)
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR_MANAGER', 'SUPER_ADMIN')")
    public ResponseEntity<ApplicationResponse> getApplicationById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                applicationService.getApplicationById(id)
        );
    }

    @GetMapping("/posting/{postingId}")
    @PreAuthorize("hasAnyRole('HR_MANAGER', 'SUPER_ADMIN')")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsByPosting(
            @PathVariable UUID postingId
    ) {
        return ResponseEntity.ok(
                applicationService.getApplicationsByPosting(postingId)
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR_MANAGER', 'SUPER_ADMIN')")
    public ResponseEntity<ApplicationResponse> getApplicationById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                applicationService.getApplicationById(id)
        );
    }

    @PatchMapping("/{applicationId}/stage")
    @PreAuthorize("hasAnyRole('HR_MANAGER', 'SUPER_ADMIN')")
    public ResponseEntity<ApplicationResponse> updateStage(
            @PathVariable UUID applicationId,
            @RequestBody UpdateStageRequest request
    ) {
        ApplicationResponse response =
                applicationService.updateStage(applicationId, request.getStage());
        return ResponseEntity.ok(response);
    }
}