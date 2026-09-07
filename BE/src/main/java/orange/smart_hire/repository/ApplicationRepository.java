package orange.smart_hire.repository;

import orange.smart_hire.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import orange.smart_hire.enums.ApplicationStage;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ApplicationRepository extends JpaRepository<Application, UUID> {

    boolean existsByPostingIdAndCandidateId(
            UUID postingId,
            UUID candidateId
    );

    List<Application> findByCandidateId(UUID candidateId);
    List<Application> findByPostingId(UUID postingId);
    @Query("SELECT a FROM Application a WHERE a.postingId = :postingId " +
            "AND (:stage IS NULL OR a.stage = :stage)")
    List<Application> findByPostingIdAndOptionalStage(@Param("postingId") UUID postingId,
                                                      @Param("stage") ApplicationStage stage);
}