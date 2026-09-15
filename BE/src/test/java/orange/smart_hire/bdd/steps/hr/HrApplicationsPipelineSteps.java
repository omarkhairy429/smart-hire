package orange.smart_hire.bdd.steps.hr;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import orange.smart_hire.dto.ApplicationResponse;
import orange.smart_hire.enums.ApplicationStage;
import orange.smart_hire.enums.UserRole;
import orange.smart_hire.model.Application;
import orange.smart_hire.model.Posting;
import orange.smart_hire.model.User;
import orange.smart_hire.repository.ApplicationRepository;
import orange.smart_hire.repository.PostingRepository;
import orange.smart_hire.repository.UserRepository;
import orange.smart_hire.service.ApplicationService;
import orange.smart_hire.service.NotificationService;
import orange.smart_hire.utils.SecurityUtils;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class HrApplicationsPipelineSteps {

    private final ApplicationRepository applicationRepository = Mockito.mock(ApplicationRepository.class);
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final PostingRepository postingRepository = Mockito.mock(PostingRepository.class);
    private final NotificationService notificationService = Mockito.mock(NotificationService.class);

    private final ApplicationService applicationService = new ApplicationService(
            applicationRepository, userRepository, postingRepository, notificationService);

    private MockedStatic<SecurityUtils> mockedSecurityUtils;
    private ApplicationResponse applicationResponse;
    private String csvResult;
    private Exception thrownException;

    @Before("@pipeline")
    public void setUp() {
        Mockito.reset(applicationRepository, userRepository, postingRepository, notificationService);
        applicationResponse = null;
        csvResult = null;
        thrownException = null;
        mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class);
    }

    @After("@pipeline")
    public void tearDown() {
        if (mockedSecurityUtils != null && !mockedSecurityUtils.isClosed()) {
            mockedSecurityUtils.close();
        }
    }

    @Given("the logged-in user is an HR Manager in company {string}")
    public void setup_hr(String company) {
        User hr = new User();
        hr.setId(UUID.randomUUID());
        hr.setRole(UserRole.HR_MANAGER);
        hr.setCompanyName(company);
        mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(hr);
    }

    @Given("an application exists with ID {string} in stage {string}")
    public void setup_application(String id, String stage) {
        Application app = new Application();
        app.setId(UUID.fromString(id));
        app.setStage(ApplicationStage.valueOf(stage));
        app.setCandidateId(UUID.randomUUID());

        when(applicationRepository.findById(app.getId())).thenReturn(Optional.of(app));
        when(applicationRepository.save(any())).thenReturn(app);
    }

    @When("the HR Manager updates the application stage to {string}")
    public void update_stage(String newStage) {
        try {
            applicationResponse = applicationService.updateStage(
                    UUID.fromString("11111111-1111-1111-1111-111111111111"),
                    ApplicationStage.valueOf(newStage)
            );
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("the application stage should become {string}")
    public void verify_stage(String expected) {
        assertNull(thrownException, "Did not expect an exception");
        assertEquals(ApplicationStage.valueOf(expected), applicationResponse.getStage());
    }

    @And("a notification for {string} should be sent to the candidate")
    public void verify_notification(String notifType) {
        verify(notificationService, times(1)).sendNotification(
                any(), eq(orange.smart_hire.enums.NotificationType.valueOf(notifType)), anyString(), anyString(), any()
        );
    }

    @Given("a posting exists for company {string} with ID {string}")
    public void setup_posting(String company, String id) {
        Posting posting = new Posting();
        posting.setId(UUID.fromString(id));
        posting.setCompany(company);
        when(postingRepository.findById(posting.getId())).thenReturn(Optional.of(posting));
    }

    @Given("there are {int} applications for this posting")
    public void setup_applications_for_posting(int count) {
        Application app1 = new Application();
        app1.setCreatedAt(LocalDateTime.now());
        Application app2 = new Application();
        app2.setCreatedAt(LocalDateTime.now());
        when(applicationRepository.findByPostingIdAndOptionalStage(any(), any()))
                .thenReturn(List.of(app1, app2));
    }

    @When("the HR Manager requests to export applications for this posting")
    public void export_csv() {
        try {
            csvResult = applicationService.exportApplicationsAsCsv(
                    UUID.fromString("22222222-2222-2222-2222-222222222222"), null, "createdAt", "asc"
            );
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a CSV string containing the application data should be generated")
    public void verify_csv() {
        assertNull(thrownException, "Did not expect an exception");
        assertNotNull(csvResult, "The generated CSV result should not be null");
        assertFalse(csvResult.trim().isEmpty(), "The generated CSV string should not be empty");
    }

    @When("the HR Manager requests applications for this posting")
    public void get_apps() {
        try {
            applicationService.getApplicationsForPosting(
                    UUID.fromString("33333333-3333-3333-3333-333333333333"), null, "createdAt", "asc"
            );
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("the pipeline action should fail with a {word} error {string}")
    public void pipeline_action_fails(String errorType, String expectedMsg) {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        String message = getExceptionMessage(thrownException);
        assertTrue(message.contains(expectedMsg), "Expected message to contain: '" + expectedMsg + "' but was: '" + message + "'");
    }

    private String getExceptionMessage(Exception e) {
        if (e instanceof ResponseStatusException rse && rse.getReason() != null) {
            return rse.getReason();
        }
        return e.getMessage() != null ? e.getMessage() : "";
    }
}