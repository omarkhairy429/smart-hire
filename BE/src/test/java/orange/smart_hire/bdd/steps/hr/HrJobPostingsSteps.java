package orange.smart_hire.bdd.steps.hr;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import orange.smart_hire.dto.PostingRequest;
import orange.smart_hire.dto.PostingResponse;
import orange.smart_hire.enums.LocationType;
import orange.smart_hire.enums.PostingStatus;
import orange.smart_hire.enums.UserRole;
import orange.smart_hire.model.Posting;
import orange.smart_hire.model.User;
import orange.smart_hire.repository.PostingRepository;
import orange.smart_hire.repository.UserRepository;
import orange.smart_hire.service.PostingService;
import orange.smart_hire.utils.SecurityUtils;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class HrJobPostingsSteps {

    private final PostingRepository postingRepository = Mockito.mock(PostingRepository.class);
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final PostingService postingService = new PostingService(postingRepository, userRepository);

    private MockedStatic<SecurityUtils> mockedSecurityUtils;
    private User loggedInHr;
    private Posting mockPosting;
    private PostingResponse postingResponse;
    private ResponseStatusException thrownException;

    @Before
    public void setUp() {
        Mockito.reset(postingRepository, userRepository);
        postingResponse = null;
        thrownException = null;
        mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class);
    }

    @After
    public void tearDown() {
        if (mockedSecurityUtils != null && !mockedSecurityUtils.isClosed()) {
            mockedSecurityUtils.close();
        }
    }

    @Given("the logged-in user is an HR Manager named {string} with ID {string}")
    public void setup_logged_in_hr(String name, String idStr) {
        loggedInHr = new User();
        loggedInHr.setId(UUID.fromString(idStr));
        loggedInHr.setFirstName(name.split(" ")[0]);
        loggedInHr.setRole(UserRole.HR_MANAGER);
        mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(loggedInHr);
    }

    @When("the HR Manager creates a draft posting titled {string} for company {string}")
    public void create_draft(String title, String company) {
        PostingRequest request = new PostingRequest();
        request.setTitle(title);
        request.setCompany(company);

        when(postingRepository.save(any(Posting.class))).thenAnswer(invocation -> {
            Posting saved = invocation.getArgument(0);
            saved.setId(UUID.randomUUID());
            return saved;
        });

        postingResponse = postingService.createDraft(request);
    }

    @Then("the posting should be saved successfully")
    public void verify_saved() {
        assertNotNull(postingResponse);
        verify(postingRepository, times(1)).save(any(Posting.class));
    }

    @And("the posting status should be {string}")
    public void verify_status(String expectedStatus) {
        assertEquals(PostingStatus.valueOf(expectedStatus), postingResponse.getStatus());
    }

    @Given("a {word} posting exists with ID {string} owned by the HR Manager")
    public void existing_posting_owned(String status, String id) {
        mockPosting = new Posting();
        mockPosting.setId(UUID.fromString(id));
        mockPosting.setHrManager(loggedInHr);
        mockPosting.setStatus(PostingStatus.valueOf(status.toUpperCase()));
        when(postingRepository.findById(mockPosting.getId())).thenReturn(Optional.of(mockPosting));
    }

    @And("the posting has all required fields filled")
    public void filling_required_fields() {
        mockPosting.setCompany("TalentBridge");
        mockPosting.setDescription("Good job");
        mockPosting.setSkillsRequired(List.of("Java", "Spring"));
        mockPosting.setLocationType(LocationType.ON_SITE);
    }

    @And("the posting is missing a description")
    public void missing_description() {
        filling_required_fields();
        mockPosting.setDescription("");
    }

    @Given("a published posting exists with ID {string} owned by another HR Manager")
    public void existing_posting_not_owned(String id) {
        User otherHr = new User();
        otherHr.setId(UUID.randomUUID());

        mockPosting = new Posting();
        mockPosting.setId(UUID.fromString(id));
        mockPosting.setHrManager(otherHr);
        mockPosting.setStatus(PostingStatus.PUBLISHED);

        when(postingRepository.findById(mockPosting.getId())).thenReturn(Optional.of(mockPosting));
    }

    @When("the HR Manager publishes the posting")
    public void publish_posting() {
        try {
            when(postingRepository.save(any(Posting.class))).thenReturn(mockPosting);
            postingResponse = postingService.publish(mockPosting.getId());
        } catch (ResponseStatusException e) {
            thrownException = e;
        }
    }

    @When("the HR Manager closes the posting")
    public void close_posting() {
        try {
            postingResponse = postingService.close(mockPosting.getId());
        } catch (ResponseStatusException e) {
            thrownException = e;
        }
    }

    @Then("the posting status should become {string}")
    public void status_becomes(String expected) {
        assertNull(thrownException);
        assertEquals(PostingStatus.valueOf(expected), postingResponse.getStatus());
    }

    @Then("the posting action should fail with a {word} error {string}")
    public void action_fails(String errorType, String expectedMsg) {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException.getReason().contains(expectedMsg));
    }
}