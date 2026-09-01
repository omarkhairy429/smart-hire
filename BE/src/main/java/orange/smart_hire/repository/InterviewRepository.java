package orange.smart_hire.repository;

import orange.smart_hire.model.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, UUID> {

    List<Interview> findByApplicationId(UUID applicationId);

    List<Interview> findByInterviewerIdAndScheduledAtAfterOrderByScheduledAtAsc(
            UUID interviewerId,
            LocalDateTime from
    );
}
