package orange.smart_hire.repository;

import orange.smart_hire.enums.LocationType;
import orange.smart_hire.enums.PostingStatus;
import orange.smart_hire.model.Posting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PostingRepository extends JpaRepository<Posting, UUID> {

    List<Posting> findByStatus(PostingStatus status);

    List<Posting> findByHrManagerId(UUID hrManagerId);

    @Query("SELECT p FROM Posting p WHERE p.status = orange.smart_hire.enums.PostingStatus.PUBLISHED " +
            "AND (:keyword = '' OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:location = '' OR LOWER(p.location) LIKE LOWER(CONCAT('%', :location, '%'))) " +
            "AND (:locationType IS NULL OR p.locationType = :locationType) " +
            "AND (:company = '' OR LOWER(p.company) LIKE LOWER(CONCAT('%', :company, '%')))")
    List<Posting> searchPublishedPostings(@Param("keyword") String keyword,
                                          @Param("location") String location,
                                          @Param("locationType") LocationType locationType,
                                          @Param("company") String company);
}