package orange.smart_hire.repository;

import orange.smart_hire.enums.PostingStatus;
import orange.smart_hire.model.Posting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PostingRepository extends JpaRepository<Posting, UUID> {

    List<Posting> findByStatus(PostingStatus status);

    List<Posting> findByHrManagerId(UUID hrManagerId);
}