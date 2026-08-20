package orange.smart_hire.service.impl;

import orange.smart_hire.dto.CreatePostingRequest;
import orange.smart_hire.enums.PostingStatus;
import orange.smart_hire.enums.UserRole;
import orange.smart_hire.model.Posting;
import orange.smart_hire.model.User;
import orange.smart_hire.repository.PostingRepository;
import orange.smart_hire.repository.UserRepository;
import orange.smart_hire.service.PostingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class PostingServiceImpl implements PostingService {

    private final PostingRepository postingRepository;
    private final UserRepository userRepository;

    public PostingServiceImpl(
            PostingRepository postingRepository,
            UserRepository userRepository
    ) {
        this.postingRepository = postingRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Posting createPosting(
            CreatePostingRequest request,
            UUID hrManagerId
    ) {

        User hrManager = userRepository.findById(hrManagerId)
                .orElseThrow(() ->
                        new RuntimeException("HR Manager not found")
                );

        if (hrManager.getRole() != UserRole.HR_MANAGER) {
            throw new RuntimeException(
                    "Only HR Managers can create job postings"
            );
        }

        if (!hrManager.isActive()) {
            throw new RuntimeException(
                    "HR Manager account is inactive"
            );
        }

        Posting posting = new Posting();

        posting.setHrManager(hrManager);
        posting.setTitle(request.getTitle());
        posting.setDescription(request.getDescription());
        posting.setSkillsRequired(request.getSkillsRequired());
        posting.setLocationType(request.getLocationType());
        posting.setLocation(request.getLocation());
        posting.setDeadline(request.getDeadline());

        posting.setStatus(PostingStatus.DRAFT);

        return postingRepository.save(posting);
    }
}