package orange.smart_hire.controller;

import java.util.List;
import java.util.UUID;

import orange.smart_hire.dto.PipelineResponse;
import orange.smart_hire.dto.PostingResponse;
import orange.smart_hire.service.ApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import orange.smart_hire.dto.PostingRequest;
import orange.smart_hire.service.PostingService;

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

    @GetMapping("/{id}")
    public PostingResponse getPostingById(@PathVariable UUID id) {
        return postingService.getPostingById(id);
    }

    @PreAuthorize("hasRole('HR_MANAGER')")
    @PutMapping("/{id}")
    public PostingResponse updatePosting(@PathVariable UUID id, @RequestBody PostingRequest request) {
        return postingService.updatePosting(id, request);
    }

    @PreAuthorize("hasRole('HR_MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePosting(@PathVariable UUID id) {
        postingService.deletePosting(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{postingId}/pipeline")
    @PreAuthorize("hasAnyRole('HR_MANAGER', 'SUPER_ADMIN')")
    public ResponseEntity<List<PipelineResponse>> getPipeline(
            @PathVariable UUID postingId
    ) {
        return ResponseEntity.ok(applicationService.getPipeline(postingId));
    }
}