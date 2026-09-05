package orange.smart_hire.controller;

import orange.smart_hire.dto.ApplicationResponse;
import orange.smart_hire.dto.PipelineResponse;
import orange.smart_hire.dto.PostingRequest;
import orange.smart_hire.dto.PostingResponse;
import orange.smart_hire.enums.ApplicationStage;
import orange.smart_hire.service.ApplicationService;
import orange.smart_hire.service.PostingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/postings")
@CrossOrigin(origins = "http://localhost:4200")
public class PostingController {

    private final PostingService postingService;
    private final ApplicationService applicationService;

    public PostingController(PostingService postingService, ApplicationService applicationService) {
        this.postingService = postingService;
        this.applicationService = applicationService;
    }

    @GetMapping
    public List<PostingResponse> getAllPostings() {
        return postingService.getAllPostings();
    }

    @GetMapping("/company")
    public ResponseEntity<List<PostingResponse>> getPostingsByCompany() {
        List<PostingResponse> postings = postingService.getPostingsByCompany();
        return ResponseEntity.ok(postings);
    }

    @PreAuthorize("hasAnyRole('HR_MANAGER', 'SUPER_ADMIN')")
    @PostMapping
    public PostingResponse createPosting(@RequestBody PostingRequest request) {
        return postingService.createPosting(request);
    }

    @PreAuthorize("hasAnyRole('HR_MANAGER', 'SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePosting(@PathVariable UUID id) {
        postingService.deletePosting(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public PostingResponse getPostingById(@PathVariable UUID id) {
        return postingService.getPostingById(id);
    }

    @PreAuthorize("hasAnyRole('HR_MANAGER', 'SUPER_ADMIN')")
    @PutMapping("/{id}")
    public PostingResponse updatePosting(@PathVariable UUID id, @RequestBody PostingRequest request) {
        return postingService.updatePosting(id, request);
    }


    @GetMapping("/published/{id}")
    public PostingResponse getPublishedPostingById(@PathVariable UUID id) {
        return postingService.getPublishedPostingById(id);
    }
    @PreAuthorize("hasAnyRole('HR_MANAGER', 'SUPER_ADMIN')")
    @PostMapping("/drafts")
    public PostingResponse createDraft(@RequestBody PostingRequest request) {
        return postingService.createDraft(request);
    }

    @PreAuthorize("hasRole('HR_MANAGER')")
    @PutMapping("/drafts/{id}")
    public PostingResponse updateDraft(@PathVariable UUID id, @RequestBody PostingRequest request) {
        return postingService.updateDraft(id, request);
    }
    @PreAuthorize("hasAnyRole('HR_MANAGER', 'SUPER_ADMIN')")
    @PatchMapping("/{id}/publish")
    public PostingResponse publish(@PathVariable UUID id) {
        return postingService.publish(id);
    }
    @PreAuthorize("hasAnyRole('HR_MANAGER', 'SUPER_ADMIN')")
    @PatchMapping("/{id}/close")
    public PostingResponse close(@PathVariable UUID id) {
        return postingService.close(id);
    }
    @GetMapping("/published")
    public List<PostingResponse> getPublishedPostings() {
        return postingService.getPublishedPostings();
    }

    @PreAuthorize("hasAnyRole('HR_MANAGER', 'SUPER_ADMIN')")
    @GetMapping("/mine")
    public List<PostingResponse> getMyPostings() {
        return postingService.getMyPostings();

    }

    @GetMapping("/{postingId}/pipeline")
    @PreAuthorize("hasAnyRole('HR_MANAGER', 'SUPER_ADMIN')")
    public ResponseEntity<List<PipelineResponse>> getPipeline (@PathVariable UUID postingId){
        return ResponseEntity.ok(applicationService.getPipeline(postingId));
    }

    @GetMapping("/{postingId}/applications")
    @PreAuthorize("hasAnyRole('HR_MANAGER', 'SUPER_ADMIN')")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsForPosting(
            @PathVariable UUID postingId,
            @RequestParam(required = false) ApplicationStage stage,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "asc") String dir) {
        return ResponseEntity.ok(applicationService.getApplicationsForPosting(postingId, stage, sort, dir));
    }

    @GetMapping(value = "/{postingId}/applications/export", produces = "text/csv")
    @PreAuthorize("hasAnyRole('HR_MANAGER', 'SUPER_ADMIN')")
    public ResponseEntity<String> exportApplications(
            @PathVariable UUID postingId,
            @RequestParam(required = false) ApplicationStage stage,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "asc") String dir) {
        String csv = applicationService.exportApplicationsAsCsv(postingId, stage, sort, dir);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=applications.csv")
                .body(csv);
    }

}