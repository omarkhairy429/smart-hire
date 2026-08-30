package orange.smart_hire.repository;

import orange.smart_hire.model.CandidateNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CandidateNoteRepository extends JpaRepository<CandidateNote, UUID> {

    /* Reading all notes for candidate */
    List<CandidateNote> findByCandidateIdOrderByCreatedAtDesc(UUID candidateId);
}