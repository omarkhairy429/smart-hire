package orange.smart_hire.controller;

import orange.smart_hire.dto.PipelineResponse;
import orange.smart_hire.dto.PostingRequest;
import orange.smart_hire.dto.PostingResponse;
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

    @PreAuthorize("hasRole('HR_MANAGER')")
    @PostMapping
    public PostingResponse createPosting(@RequestBody PostingRequest request) {
        return postingService.createPosting(request);
    }

    @PreAuthorize("hasRole('HR_MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePosting(@PathVariable UUID id) {
        postingService.deletePosting(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public PostingResponse getPostingById(@PathVariable UUID id) {
        return postingService.getPostingById(id);
    }

    @PreAuthorize("hasRole('HR_MANAGER')")
    @PutMapping("/{id}")
    public PostingResponse updatePosting(@PathVariable UUID id, @RequestBody PostingRequest request) {
        return postingService.updatePosting(id, request);
    }


    @GetMapping("/published/{id}")
    public PostingResponse getPublishedPostingById(@PathVariable UUID id) {
        return postingService.getPublishedPostingById(id);
    }
    @PreAuthorize("hasRole('HR_MANAGER')")
    @PostMapping("/drafts")
    public PostingResponse createDraft(@RequestBody PostingRequest request) {
        return postingService.createDraft(request);
    }

    @PreAuthorize("hasRole('HR_MANAGER')")
    @PutMapping("/drafts/{id}")
    public PostingResponse updateDraft(@PathVariable UUID id, @RequestBody PostingRequest request) {
        return postingService.updateDraft(id, request);
    }
    @PreAuthorize("hasRole('HR_MANAGER')")
    @PatchMapping("/{id}/publish")
    public PostingResponse publish(@PathVariable UUID id) {
        return postingService.publish(id);
    }
    @PreAuthorize("hasRole('HR_MANAGER')")
    @PatchMapping("/{id}/close")
    public PostingResponse close(@PathVariable UUID id) {
        return postingService.close(id);
    }
    @GetMapping("/published")
    public List<PostingResponse> getPublishedPostings() {
        return postingService.getPublishedPostings();
    }

    @PreAuthorize("hasRole('HR_MANAGER')")
    @GetMapping("/mine")
    public List<PostingResponse> getMyPostings() {
        return postingService.getMyPostings();

    }

    @GetMapping("/{postingId}/pipeline")
    @PreAuthorize("hasAnyRole('HR_MANAGER', 'SUPER_ADMIN')")
    public ResponseEntity<List<PipelineResponse>> getPipeline (@PathVariable UUID postingId){
        return ResponseEntity.ok(applicationService.getPipeline(postingId));
    }

}