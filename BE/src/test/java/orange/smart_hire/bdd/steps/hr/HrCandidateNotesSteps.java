package orange.smart_hire.bdd.steps.hr;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import orange.smart_hire.dto.CandidateNoteDto;
import orange.smart_hire.enums.UserRole;
import orange.smart_hire.model.CandidateNote;
import orange.smart_hire.model.User;
import orange.smart_hire.repository.CandidateNoteRepository;
import orange.smart_hire.repository.UserRepository;
import orange.smart_hire.service.HrCandidateNoteService;
import orange.smart_hire.utils.SecurityUtils;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class HrCandidateNotesSteps {

    private final CandidateNoteRepository noteRepository = Mockito.mock(CandidateNoteRepository.class);
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);

    private final HrCandidateNoteService noteService = new HrCandidateNoteService(noteRepository, userRepository);

    private MockedStatic<SecurityUtils> mockedSecurityUtils;
    private User loggedInHr;
    private CandidateNoteDto.Response noteResponse;
    private List<CandidateNoteDto.Response> notesList;
    private Exception thrownException;

    @Before("@notes")
    public void setUp() {
        Mockito.reset(noteRepository, userRepository);
        noteResponse = null;
        notesList = null;
        thrownException = null;

        mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class);
    }

    @After("@notes")
    public void tearDown() {
        if (mockedSecurityUtils != null && !mockedSecurityUtils.isClosed()) {
            mockedSecurityUtils.close();
        }
    }

    @Given("the logged-in user is an HR Manager named {string}")
    public void setup_logged_in_hr(String fullName) {
        String[] nameParts = fullName.split(" ");
        loggedInHr = new User();
        loggedInHr.setId(UUID.randomUUID());
        loggedInHr.setFirstName(nameParts[0]);
        loggedInHr.setLastName(nameParts.length > 1 ? nameParts[1] : "");
        loggedInHr.setRole(UserRole.HR_MANAGER);

        mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(loggedInHr);
    }

    @Given("a candidate profile exists with id {string}")
    public void candidate_exists(String idStr) {
        UUID id = UUID.fromString(idStr);
        User candidate = new User();
        candidate.setId(id);
        candidate.setRole(UserRole.CANDIDATE);

        when(userRepository.findById(id)).thenReturn(Optional.of(candidate));
    }

    @Given("a candidate profile with id {string} does not exist")
    public void candidate_does_not_exist(String idStr) {
        UUID id = UUID.fromString(idStr);
        when(userRepository.findById(id)).thenReturn(Optional.empty());
    }

    @When("the HR Manager adds a note with content {string} for candidate {string}")
    public void hr_adds_note(String content, String candidateIdStr) {
        CandidateNoteDto.Request request = new CandidateNoteDto.Request();
        request.setCandidateId(UUID.fromString(candidateIdStr));
        request.setContent(content);

        when(noteRepository.save(any(CandidateNote.class))).thenAnswer(invocation -> {
            CandidateNote savedNote = invocation.getArgument(0);
            savedNote.setId(UUID.randomUUID());
            savedNote.setCreatedAt(LocalDateTime.now());
            return savedNote;
        });

        try {
            noteResponse = noteService.addNote(request);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("the note should be saved successfully")
    public void note_saved_successfully() {
        assertNull(thrownException, "Did not expect an exception");
        assertNotNull(noteResponse);
        verify(noteRepository, times(1)).save(any(CandidateNote.class));
    }

    @And("the note response should contain the author name {string} and content {string}")
    public void verify_note_response(String expectedAuthor, String expectedContent) {
        assertEquals(expectedAuthor, noteResponse.getAuthorName());
        assertEquals(expectedContent, noteResponse.getContent());
    }

    @Then("the action should fail with a not found error {string}")
    public void action_fails_with_not_found(String expectedMsg) {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        String message = getExceptionMessage(thrownException);
        assertTrue(message.contains(expectedMsg), "Expected message to contain: '" + expectedMsg + "' but was: '" + message + "'");
        verify(noteRepository, never()).save(any()); // Ensure nothing is saved
    }

    @Given("the candidate has {int} existing notes")
    public void candidate_has_existing_notes(int count) {
        List<CandidateNote> mockNotes = new ArrayList<>();
        User mockAuthor = new User();
        mockAuthor.setFirstName("Test");
        mockAuthor.setLastName("HR");

        for (int i = 0; i < count; i++) {
            CandidateNote note = new CandidateNote();
            note.setId(UUID.randomUUID());
            note.setContent("Note " + i);
            note.setAuthor(mockAuthor);
            note.setCreatedAt(LocalDateTime.now());
            mockNotes.add(note);
        }

        when(noteRepository.findByCandidateIdOrderByCreatedAtDesc(any(UUID.class)))
                .thenReturn(mockNotes);
    }

    @When("the HR Manager requests notes for candidate {string}")
    public void hr_requests_notes(String candidateIdStr) {
        notesList = noteService.getNotesForCandidate(UUID.fromString(candidateIdStr));
    }

    @Then("exactly {int} notes should be returned")
    public void verify_returned_notes_count(int expectedCount) {
        assertNotNull(notesList);
        assertEquals(expectedCount, notesList.size());
    }

    private String getExceptionMessage(Exception e) {
        if (e instanceof ResponseStatusException rse && rse.getReason() != null) {
            return rse.getReason();
        }
        return e.getMessage() != null ? e.getMessage() : "";
    }
}