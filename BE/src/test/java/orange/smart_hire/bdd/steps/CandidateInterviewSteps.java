package orange.smart_hire.bdd.steps;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import orange.smart_hire.dto.InterviewResponse;
import orange.smart_hire.enums.InterviewFormat;
import orange.smart_hire.enums.UserRole;
import orange.smart_hire.model.Application;
import orange.smart_hire.model.Interview;
import orange.smart_hire.model.Posting;
import orange.smart_hire.model.User;
import orange.smart_hire.repository.ApplicationRepository;
import orange.smart_hire.repository.InterviewRepository;
import orange.smart_hire.repository.PostingRepository;
import orange.smart_hire.repository.UserRepository;
import orange.smart_hire.service.InterviewService;
import orange.smart_hire.service.NotificationService;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class CandidateInterviewSteps {

    private final InterviewRepository interviewRepository = Mockito.mock(InterviewRepository.class);
    private final ApplicationRepository applicationRepository = Mockito.mock(ApplicationRepository.class);
    private final PostingRepository postingRepository = Mockito.mock(PostingRepository.class);
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final NotificationService notificationService = Mockito.mock(NotificationService.class);

    private final InterviewService interviewService = new InterviewService(
            interviewRepository,
            applicationRepository,
            postingRepository,
            userRepository,
            notificationService
    );

    private UUID candidateId;
    private UUID applicationId;
    private User candidate;
    private Application application;
    private Interview interview;
    private List<InterviewResponse> interviewResponses;

    @Before
    public void setUp() {
        candidateId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        applicationId = UUID.fromString("33333333-3333-3333-3333-333333333333");

        candidate = new User();
        candidate.setId(candidateId);
        candidate.setFirstName("John");
        candidate.setLastName("Candidate");
        candidate.setEmail("john@example.com");
        candidate.setRole(UserRole.CANDIDATE);
        candidate.setActive(true);

        application = new Application();
        application.setId(applicationId);
        application.setCandidateId(candidateId);
        application.setPostingId(UUID.fromString("22222222-2222-2222-2222-222222222222"));
        application.setResumeUrl("https://example.com/resume.pdf");

        interview = new Interview();
        interview.setId(UUID.fromString("66666666-6666-6666-6666-666666666666"));
        interview.setApplicationId(applicationId);
        interview.setInterviewerId(UUID.fromString("77777777-7777-7777-7777-777777777777"));
        interview.setScheduledAt(LocalDateTime.of(2026, 9, 15, 10, 0));
        interview.setFormat(InterviewFormat.VIDEO);
        interview.setMeetingLink("https://meet.example.com/interview");

        interviewResponses = null;
    }

    @Given("an interview candidate exists with id {string}")
    public void an_interview_candidate_exists_with_id(String id) {
        candidateId = UUID.fromString(id);
        candidate.setId(candidateId);
        application.setCandidateId(candidateId);
        when(userRepository.findById(candidateId)).thenReturn(java.util.Optional.of(candidate));
    }

    @And("the candidate has an application with id {string}")
    public void the_candidate_has_an_application_with_id(String id) {
        applicationId = UUID.fromString(id);
        application.setId(applicationId);
        interview.setApplicationId(applicationId);
        when(applicationRepository.findByCandidateId(candidateId)).thenReturn(List.of(application));
    }

    @And("an interview is scheduled for that application")
    public void an_interview_is_scheduled_for_that_application() {
        when(interviewRepository.findByApplicationId(applicationId)).thenReturn(List.of(interview));
        when(userRepository.findById(interview.getInterviewerId())).thenReturn(java.util.Optional.of(interviewer()));
        when(postingRepository.findById(application.getPostingId())).thenReturn(java.util.Optional.of(posting()));
        when(applicationRepository.findById(applicationId)).thenReturn(java.util.Optional.of(application));
    }

    @When("the candidate requests their interviews")
    public void the_candidate_requests_their_interviews() {
        interviewResponses = interviewService.getInterviewsByCandidate(candidateId);
    }

    @Then("{int} interview should be returned")
    public void interview_should_be_returned(int expectedCount) {
        assertNotNull(interviewResponses);
        assertEquals(expectedCount, interviewResponses.size());
    }

    @And("the returned interview should belong to the candidate application")
    public void the_returned_interview_should_belong_to_the_candidate_application() {
        assertEquals(applicationId, interviewResponses.get(0).getApplicationId());
    }

    private User interviewer() {
        User interviewer = new User();
        interviewer.setId(interview.getInterviewerId());
        interviewer.setFirstName("Jane");
        interviewer.setLastName("Interviewer");
        interviewer.setEmail("jane@example.com");
        interviewer.setRole(UserRole.INTERVIEWER);
        interviewer.setActive(true);
        return interviewer;
    }

    private Posting posting() {
        Posting posting = new Posting();
        posting.setId(application.getPostingId());
        posting.setTitle("Java Developer");
        posting.setCompany("SmartHire");
        return posting;
    }
}
