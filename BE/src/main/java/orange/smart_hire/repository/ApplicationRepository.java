package orange.smart_hire.repository;

import orange.smart_hire.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ApplicationRepository extends JpaRepository<Application, UUID> {

    boolean existsByPostingIdAndCandidateId(
            UUID postingId,
            UUID candidateId
    );

    List<Application> findByCandidateId(UUID candidateId);
    List<Application> findByPostingId(UUID postingId);
}