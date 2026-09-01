package orange.smart_hire.service;

import lombok.RequiredArgsConstructor;
import orange.smart_hire.dto.CandidateNoteDto;
import orange.smart_hire.model.CandidateNote;
import orange.smart_hire.model.User;
import orange.smart_hire.repository.CandidateNoteRepository;
import orange.smart_hire.repository.UserRepository;
import orange.smart_hire.utils.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HrCandidateNoteService {

    private final CandidateNoteRepository noteRepository;
    private final UserRepository userRepository;

    public CandidateNoteDto.Response addNote(CandidateNoteDto.Request request) {
        // 1. Get the HR author
        User author = SecurityUtils.getCurrentUser();

        // 2. Get the candidate
        User candidate = userRepository.findById(request.getCandidateId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Candidate not found"));

        // 3. Create and save the note
        CandidateNote note = new CandidateNote();
        note.setCandidate(candidate);
        note.setAuthor(author);
        note.setContent(request.getContent());

        CandidateNote savedNote = noteRepository.save(note);

        return mapToResponse(savedNote);
    }

    public List<CandidateNoteDto.Response> getNotesForCandidate(UUID candidateId) {
        return noteRepository.findByCandidateIdOrderByCreatedAtDesc(candidateId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    private CandidateNoteDto.Response mapToResponse(CandidateNote note) {
        CandidateNoteDto.Response response = new CandidateNoteDto.Response();
        response.setId(note.getId());
        response.setContent(note.getContent());
        response.setAuthorName(note.getAuthor().getFirstName() + " " + note.getAuthor().getLastName());
        response.setCreatedAt(note.getCreatedAt());
        return response;
    }
}