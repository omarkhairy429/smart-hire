package orange.smart_hire.repository;

import orange.smart_hire.model.Posting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PostingRepository extends JpaRepository<Posting, UUID> {
}