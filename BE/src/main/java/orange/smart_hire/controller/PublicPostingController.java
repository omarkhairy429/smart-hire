package orange.smart_hire.controller;

import orange.smart_hire.dto.PostingResponse;
import orange.smart_hire.enums.LocationType;
import orange.smart_hire.service.PostingService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/public/postings")
@CrossOrigin(origins = "http://localhost:4200")
public class PublicPostingController {

    private final PostingService postingService;

    public PublicPostingController(PostingService postingService) {
        this.postingService = postingService;
    }

    @GetMapping
    public List<PostingResponse> searchPostings(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) LocationType locationType,
            @RequestParam(required = false) String company) {
        return postingService.searchPublishedPostings(keyword, location, locationType, company);
    }
}