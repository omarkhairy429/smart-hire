package orange.smart_hire.bdd.steps.candidates;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import orange.smart_hire.dto.ApplyRequest;
import orange.smart_hire.dto.ApplicationResponse;
import orange.smart_hire.enums.ApplicationStage;
import orange.smart_hire.enums.ApplicationStatus;
import orange.smart_hire.enums.UserRole;
import orange.smart_hire.model.Posting;
import orange.smart_hire.model.User;
import orange.smart_hire.repository.ApplicationRepository;
import orange.smart_hire.repository.PostingRepository;
import orange.smart_hire.repository.UserRepository;
import orange.smart_hire.service.ApplicationService;
import orange.smart_hire.service.NotificationService;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class CandidateApplicationSteps {

    private final ApplicationRepository applicationRepository = Mockito.mock(ApplicationRepository.class);
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final PostingRepository postingRepository = Mockito.mock(PostingRepository.class);
    private final NotificationService notificationService = Mockito.mock(NotificationService.class);

    private final ApplicationService applicationService = new ApplicationService(
            applicationRepository,
            userRepository,
            postingRepository,
            notificationService
    );

    private UUID candidateId;
    private UUID postingId;
    private User candidate;
    private Posting posting;
    private ApplicationResponse applicationResponse;
    private List<ApplicationResponse> applicationResponses;
    private Exception thrownException;

    @Before
    public void setUp() {
        candidateId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        postingId = UUID.fromString("22222222-2222-2222-2222-222222222222");

        candidate = new User();
        candidate.setId(candidateId);
        candidate.setFirstName("John");
        candidate.setLastName("Candidate");
        candidate.setEmail("john@example.com");
        candidate.setRole(UserRole.CANDIDATE);
        candidate.setActive(true);

        User hrManager = new User();
        hrManager.setId(UUID.fromString("44444444-4444-4444-4444-444444444444"));
        hrManager.setFirstName("HR");
        hrManager.setLastName("Manager");
        hrManager.setEmail("hr@example.com");
        hrManager.setRole(UserRole.HR_MANAGER);
        hrManager.setActive(true);

        posting = new Posting();
        posting.setId(postingId);
        posting.setTitle("Java Developer");
        posting.setCompany("SmartHire");
        posting.setHrManager(hrManager);

        applicationResponse = null;
        applicationResponses = null;
        thrownException = null;
    }

    @Given("a candidate exists with id {string}")
    public void a_candidate_exists_with_id(String id) {
        candidateId = UUID.fromString(id);
        candidate.setId(candidateId);
        when(userRepository.findById(candidateId)).thenReturn(Optional.of(candidate));
    }

    @And("a job posting exists with id {string}")
    public void a_job_posting_exists_with_id(String id) {
        postingId = UUID.fromString(id);
        posting.setId(postingId);
        when(postingRepository.findById(postingId)).thenReturn(Optional.of(posting));
    }

    @When("the candidate applies with resume {string} and cover letter {string}")
    public void the_candidate_applies(String resumeUrl, String coverLetter) {
        ApplyRequest request = new ApplyRequest();
        request.setPostingId(postingId);
        request.setResumeUrl(resumeUrl);
        request.setCoverLetter(coverLetter);
        request.setExperienceSummary("3 years of Java experience");

        try {
            when(applicationRepository.save(any())).thenAnswer(invocation -> {
                var saved = invocation.getArgument(0, orange.smart_hire.model.Application.class);
                saved.setId(UUID.fromString("55555555-5555-5555-5555-555555555555"));
                return saved;
            });
            applicationResponse = applicationService.apply(request, candidateId);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("the application should be created")
    public void the_application_should_be_created() {
        assertNull(thrownException);
        assertNotNull(applicationResponse);
        assertNotNull(applicationResponse.getId());
        assertEquals(postingId, applicationResponse.getPostingId());
        assertEquals(candidateId, applicationResponse.getCandidateId());
    }

    @And("the application stage should be {string}")
    public void the_application_stage_should_be(String expectedStage) {
        assertEquals(ApplicationStage.valueOf(expectedStage), applicationResponse.getStage());
    }

    @And("the application status should be {string}")
    public void the_application_status_should_be(String expectedStatus) {
        assertEquals(ApplicationStatus.valueOf(expectedStatus), applicationResponse.getStatus());
    }

    @And("the candidate has already applied to the job")
    public void the_candidate_has_already_applied_to_the_job() {
        when(applicationRepository.existsByPostingIdAndCandidateId(postingId, candidateId))
                .thenReturn(true);
    }

    @Then("the application should be rejected with message {string}")
    public void the_application_should_be_rejected_with_message(String expectedMessage) {
        assertNotNull(thrownException);
        assertEquals(expectedMessage, thrownException.getMessage());
    }

    @And("the candidate has {int} applications")
    public void the_candidate_has_applications(int count) {
        var applications = new java.util.ArrayList<orange.smart_hire.model.Application>();
        for (int i = 0; i < count; i++) {
            var application = new orange.smart_hire.model.Application();
            application.setId(UUID.nameUUIDFromBytes(("application-" + i).getBytes()));
            application.setCandidateId(candidateId);
            application.setPostingId(UUID.nameUUIDFromBytes(("posting-" + i).getBytes()));
            application.setResumeUrl("https://example.com/resume-" + i + ".pdf");
            application.setStage(ApplicationStage.APPLIED);
            application.setStatus(ApplicationStatus.IN_REVIEW);
            applications.add(application);
        }

        when(applicationRepository.findByCandidateId(candidateId)).thenReturn(applications);
    }

    @When("the candidate requests their applications")
    public void the_candidate_requests_their_applications() {
        applicationResponses = applicationService.getMyApplications(candidateId);
    }

    @Then("{int} applications should be returned")
    public void applications_should_be_returned(int expectedCount) {
        assertNotNull(applicationResponses);
        assertEquals(expectedCount, applicationResponses.size());
    }

    @And("every returned application should belong to the candidate")
    public void every_returned_application_should_belong_to_the_candidate() {
        assertTrue(applicationResponses.stream()
                .allMatch(response -> candidateId.equals(response.getCandidateId())));
    }
}
