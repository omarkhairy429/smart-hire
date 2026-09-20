package orange.smart_hire.bdd.steps.hr;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import orange.smart_hire.dto.InterviewResponse;
import orange.smart_hire.dto.ScheduleInterviewRequest;
import orange.smart_hire.enums.InterviewFormat;
import orange.smart_hire.enums.NotificationType;
import orange.smart_hire.enums.UserRole;
import orange.smart_hire.model.Application;
import orange.smart_hire.model.Interview;
import orange.smart_hire.model.User;
import orange.smart_hire.repository.ApplicationRepository;
import orange.smart_hire.repository.InterviewRepository;
import orange.smart_hire.repository.PostingRepository;
import orange.smart_hire.repository.UserRepository;
import orange.smart_hire.service.InterviewService;
import orange.smart_hire.service.NotificationService;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class HrInterviewsSteps {

    private final InterviewRepository interviewRepository = Mockito.mock(InterviewRepository.class);
    private final ApplicationRepository applicationRepository = Mockito.mock(ApplicationRepository.class);
    private final PostingRepository postingRepository = Mockito.mock(PostingRepository.class);
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final NotificationService notificationService = Mockito.mock(NotificationService.class);

    private final InterviewService interviewService = new InterviewService(
            interviewRepository, applicationRepository, postingRepository, userRepository, notificationService
    );

    private InterviewResponse interviewResponse;
    private Exception thrownException;
    private UUID appId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private UUID intId = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private UUID scheduleId = UUID.fromString("33333333-3333-3333-3333-333333333333");

    @Before("@interviews")
    public void setUp() {
        Mockito.reset(interviewRepository, applicationRepository, userRepository, notificationService);
        interviewResponse = null;
        thrownException = null;
    }

    @Given("the logged-in user is an HR Manager")
    public void no_op_security_mock() {
        // Not strictly needed since InterviewService doesn't use SecurityUtils.getCurrentUser() for scheduling
    }

    @Given("an application exists with ID {string}")
    public void app_exists(String id) {
        Application app = new Application();
        app.setId(UUID.fromString(id));
        app.setCandidateId(UUID.randomUUID());
        when(applicationRepository.findById(app.getId())).thenReturn(Optional.of(app));
    }

    @And("an interviewer exists with ID {string}")
    public void int_exists(String id) {
        User interviewer = new User();
        interviewer.setId(UUID.fromString(id));
        interviewer.setRole(UserRole.INTERVIEWER);
        when(userRepository.findById(interviewer.getId())).thenReturn(Optional.of(interviewer));
    }

    @When("the HR Manager schedules an {string} interview")
    public void schedule_valid(String format) {
        ScheduleInterviewRequest req = new ScheduleInterviewRequest();
        req.setInterviewerId(intId);
        req.setFormat(InterviewFormat.valueOf(format));
        req.setScheduledAt(LocalDateTime.now().plusDays(2));
        req.setLocation("Office 12");

        when(interviewRepository.save(any())).thenAnswer(i -> {
            Interview saved = i.getArgument(0);
            saved.setId(UUID.randomUUID());
            return saved;
        });

        try {
            interviewResponse = interviewService.schedule(appId, req);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @When("the HR Manager schedules a {string} interview without providing a meeting link")
    public void schedule_invalid(String format) {
        ScheduleInterviewRequest req = new ScheduleInterviewRequest();
        req.setInterviewerId(intId);
        req.setFormat(InterviewFormat.valueOf(format));
        req.setScheduledAt(LocalDateTime.now().plusDays(2));

        try {
            interviewService.schedule(appId, req);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("the interview should be saved successfully")
    public void verify_saved() {
        assertNull(thrownException, "Did not expect an exception");
        assertNotNull(interviewResponse);
        verify(interviewRepository, times(1)).save(any(Interview.class));
    }

    @And("notifications should be sent to both candidate and interviewer")
    public void verify_notifs() {
        verify(notificationService, times(2)).sendNotification(
                any(), eq(NotificationType.INTERVIEW_SCHEDULED), anyString(), anyString(), any()
        );
    }

    @Given("a scheduled interview exists with ID {string}")
    public void existing_interview(String id) {
        Interview interview = new Interview();
        interview.setId(UUID.fromString(id));
        interview.setApplicationId(appId);
        interview.setInterviewerId(intId);
        when(interviewRepository.findById(interview.getId())).thenReturn(Optional.of(interview));

        Application app = new Application();
        app.setCandidateId(UUID.randomUUID());
        when(applicationRepository.findById(appId)).thenReturn(Optional.of(app));
    }

    @When("the HR Manager cancels the interview")
    public void cancel_interview() {
        try {
            interviewService.cancel(scheduleId);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("the interview should be deleted")
    public void verify_deleted() {
        assertNull(thrownException, "Did not expect an exception");
        verify(interviewRepository, times(1)).delete(any(Interview.class));
    }

    @And("cancellation notifications should be sent")
    public void verify_cancel_notifs() {
        verify(notificationService, times(2)).sendNotification(
                any(), eq(NotificationType.INTERVIEW_CANCELLED), anyString(), anyString(), any()
        );
    }

    @Then("the interview action should fail with a {word} error {string}")
    public void interview_action_fails(String errorType, String expectedMsg) {
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