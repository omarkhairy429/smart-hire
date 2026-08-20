package orange.smart_hire.controller;

import jakarta.validation.Valid;
import orange.smart_hire.dto.CreatePostingRequest;
import orange.smart_hire.dto.PostingResponse;
import orange.smart_hire.model.Posting;
import orange.smart_hire.service.PostingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/postings")
public class PostingController {

    private final PostingService postingService;

    public PostingController(PostingService postingService) {
        this.postingService = postingService;
    }

    @PostMapping
    public ResponseEntity<PostingResponse> createPosting(
            @Valid @RequestBody CreatePostingRequest request,
            @RequestParam UUID hrManagerId
    ) {

        Posting posting = postingService.createPosting(
                request,
                hrManagerId
        );

        PostingResponse response = mapToResponse(posting);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    private PostingResponse mapToResponse(Posting posting) {

        PostingResponse response = new PostingResponse();

        response.setId(posting.getId());
        response.setHrManagerId(posting.getHrManager().getId());
        response.setTitle(posting.getTitle());
        response.setDescription(posting.getDescription());
        response.setSkillsRequired(posting.getSkillsRequired());
        response.setLocationType(posting.getLocationType());
        response.setLocation(posting.getLocation());
        response.setStatus(posting.getStatus());
        response.setDeadline(posting.getDeadline());
        response.setCreatedAt(posting.getCreatedAt());
        response.setUpdatedAt(posting.getUpdatedAt());

        return response;
    }
}