package orange.smart_hire.service;

import orange.smart_hire.dto.PostingRequest;
import orange.smart_hire.model.Posting;
import orange.smart_hire.model.User;
import orange.smart_hire.repository.PostingRepository;
import orange.smart_hire.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<Posting> getAllPostings() {
        return postingRepository.findAll();
    }

    public Posting createPosting(PostingRequest request) {

        User hrManager = userRepository.findById(request.getHrManagerId())
                .orElseThrow(() -> new RuntimeException("HR Manager not found"));

        Posting posting = new Posting();

        posting.setHrManager(hrManager);
        posting.setTitle(request.getTitle());
        posting.setDescription(request.getDescription());
        posting.setSkillsRequired(request.getSkillsRequired());
        posting.setLocationType(request.getLocationType());
        posting.setLocation(request.getLocation());
        posting.setDeadline(request.getDeadline());

        return postingRepository.save(posting);
    }
}