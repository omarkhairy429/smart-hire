package orange.smart_hire.service;

import orange.smart_hire.dto.PostingRequest;
import orange.smart_hire.dto.PostingResponse;
import orange.smart_hire.model.Posting;
import orange.smart_hire.model.User;
import orange.smart_hire.repository.PostingRepository;
import orange.smart_hire.repository.UserRepository;
import orange.smart_hire.utils.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class PostingService {

    private final PostingRepository postingRepository;
    private final UserRepository userRepository;

    public PostingService(
            PostingRepository postingRepository,
            UserRepository userRepository) {
        this.postingRepository = postingRepository;
        this.userRepository = userRepository;
    }

    public List<PostingResponse> getAllPostings() {
        return postingRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public PostingResponse createPosting(PostingRequest request) {
        User hrManager = SecurityUtils.getCurrentUser();

        Posting posting = new Posting();
        posting.setHrManager(hrManager);
        posting.setTitle(request.getTitle());
        posting.setDescription(request.getDescription());
        posting.setSkillsRequired(request.getSkillsRequired());
        posting.setLocationType(request.getLocationType());
        posting.setLocation(request.getLocation());
        posting.setDeadline(request.getDeadline());

        Posting savedPosting = postingRepository.save(posting);
        return mapToResponse(savedPosting);
    }

    private PostingResponse mapToResponse(Posting posting) {
        PostingResponse response = new PostingResponse();
        response.setId(posting.getId());
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
    public PostingResponse getPostingById(UUID id) {
        Posting posting = findPostingOrThrow(id);
        return mapToResponse(posting);
    }

    public PostingResponse updatePosting(UUID id, PostingRequest request) {
        Posting posting = findPostingOrThrow(id);
        assertOwnedByCurrentUser(posting);

        posting.setTitle(request.getTitle());
        posting.setDescription(request.getDescription());
        posting.setSkillsRequired(request.getSkillsRequired());
        posting.setLocationType(request.getLocationType());
        posting.setLocation(request.getLocation());
        posting.setDeadline(request.getDeadline());

        Posting savedPosting = postingRepository.save(posting);
        return mapToResponse(savedPosting);
    }

    public void deletePosting(UUID id) {
        Posting posting = findPostingOrThrow(id);
        assertOwnedByCurrentUser(posting);

        postingRepository.delete(posting);
    }

    private Posting findPostingOrThrow(UUID id) {
        return postingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Posting WAS not found"));
    }

    private void assertOwnedByCurrentUser(Posting posting) {
        User currentUser = SecurityUtils.getCurrentUser();
        if (!posting.getHrManager().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "you cannot modify this posting");
        }
    }
}