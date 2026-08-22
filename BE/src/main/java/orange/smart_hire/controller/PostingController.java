package orange.smart_hire.controller;

import java.util.List;

import orange.smart_hire.dto.PostingResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import orange.smart_hire.dto.PostingRequest;
import orange.smart_hire.model.Posting;
import orange.smart_hire.service.PostingService;

@RestController
@RequestMapping("/api/postings")
@CrossOrigin(origins = "http://localhost:4200")
public class PostingController {

    private final PostingService postingService;

    public PostingController(PostingService postingService) {
        this.postingService = postingService;
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
}