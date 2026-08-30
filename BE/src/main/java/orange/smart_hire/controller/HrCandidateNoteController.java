package orange.smart_hire.controller;

import lombok.RequiredArgsConstructor;
import orange.smart_hire.dto.CandidateNoteDto;
import orange.smart_hire.service.HrCandidateNoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/hr/notes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('HR_MANAGER') or hasRole('SUPER_ADMIN')")
public class HrCandidateNoteController {

    private final HrCandidateNoteService noteService;

    @PostMapping
    public ResponseEntity<CandidateNoteDto.Response> addNote(
            @RequestBody CandidateNoteDto.Request request) {


        CandidateNoteDto.Response response = noteService.addNote(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<List<CandidateNoteDto.Response>> getNotesForCandidate(
            @PathVariable UUID candidateId) {

        List<CandidateNoteDto.Response> notes = noteService.getNotesForCandidate(candidateId);

        return ResponseEntity.ok(notes);
    }
}