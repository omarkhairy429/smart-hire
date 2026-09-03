package orange.smart_hire.repository;

import orange.smart_hire.model.InterviewFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InterviewFeedbackRepository extends JpaRepository<InterviewFeedback, UUID> {

    List<InterviewFeedback> findByInterviewId(UUID interviewId);

    Optional<InterviewFeedback> findByInterviewIdAndInterviewerId(UUID interviewId, UUID interviewerId);

    boolean existsByInterviewIdAndInterviewerId(UUID interviewId, UUID interviewerId);
}
