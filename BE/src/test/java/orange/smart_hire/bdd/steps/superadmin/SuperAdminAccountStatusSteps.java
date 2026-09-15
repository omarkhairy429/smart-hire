package orange.smart_hire.bdd.steps.superadmin;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import orange.smart_hire.dto.StaffResponse;
import orange.smart_hire.enums.UserRole;
import orange.smart_hire.model.User;
import orange.smart_hire.repository.ApplicationRepository;
import orange.smart_hire.repository.PostingRepository;
import orange.smart_hire.repository.UserRepository;
import orange.smart_hire.service.AuditLogService;
import orange.smart_hire.service.EmailService;
import orange.smart_hire.service.SuperAdminService;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class SuperAdminAccountStatusSteps {

    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final AuditLogService auditLogService = Mockito.mock(AuditLogService.class);
    private final PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
    private final EmailService emailService = Mockito.mock(EmailService.class);
    private final PostingRepository postingRepository = Mockito.mock(PostingRepository.class);
    private final ApplicationRepository applicationRepository = Mockito.mock(ApplicationRepository.class);

    private final SuperAdminService superAdminService = new SuperAdminService(
            userRepository, passwordEncoder, emailService, auditLogService, postingRepository, applicationRepository
    );

    private UUID targetUserId;
    private User targetUser;
    private StaffResponse staffResponse;
    private Exception thrownException;

    @Before
    public void setUp() {
        targetUserId = null;
        targetUser = null;
        staffResponse = null;
        thrownException = null;
        Mockito.reset(userRepository, auditLogService, passwordEncoder, emailService, postingRepository, applicationRepository);
    }

    @Given("an active staff member exists with id {string} and role {string}")
    public void an_active_staff_member_exists(String id, String roleStr) {
        setupUser(id, roleStr, true);
    }

    @Given("a deactivated staff member exists with id {string} and role {string}")
    public void a_deactivated_staff_member_exists(String id, String roleStr) {
        setupUser(id, roleStr, false);
    }

    @Given("a user exists with id {string} and role {string}")
    public void a_user_exists_with_role(String id, String roleStr) {
        setupUser(id, roleStr, true);
    }

    private void setupUser(String id, String roleStr, boolean isActive) {
        targetUserId = UUID.fromString(id);
        targetUser = new User();
        targetUser.setId(targetUserId);
        targetUser.setEmail("staff@talentbridge.com");
        targetUser.setRole(UserRole.valueOf(roleStr));
        targetUser.setActive(isActive);

        when(userRepository.findById(targetUserId)).thenReturn(Optional.of(targetUser));
        when(userRepository.save(any(User.class))).thenReturn(targetUser);
    }

    @When("the Super Admin requests to deactivate the staff member")
    public void deactivate_staff() {
        try {
            staffResponse = superAdminService.deactivateStaff(targetUserId);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @When("the Super Admin requests to reactivate the staff member")
    public void reactivate_staff() {
        try {
            staffResponse = superAdminService.reactivateStaff(targetUserId);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("the staff account should become inactive")
    public void verify_inactive() {
        assertNull(thrownException, "Did not expect an exception, but got: " + (thrownException != null ? thrownException.getMessage() : ""));
        assertNotNull(staffResponse);
        assertFalse(staffResponse.isActive(), "Staff member should be inactive");
        verify(userRepository, times(1)).save(targetUser);
    }

    @Then("the staff account should become active")
    public void verify_active() {
        assertNull(thrownException, "Did not expect an exception, but got: " + (thrownException != null ? thrownException.getMessage() : ""));
        assertNotNull(staffResponse);
        assertTrue(staffResponse.isActive(), "Staff member should be active");
        verify(userRepository, times(1)).save(targetUser);
    }

    @And("an audit log for {string} should be created")
    public void verify_audit_log(String action) {
        verify(auditLogService, times(1)).log(
                eq(action),
                eq("User"),
                eq(targetUserId),
                any(Map.class)
        );
    }

    @Then("the action should fail with a conflict error {string}")
    public void verify_conflict_error(String expectedMsg) {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        if (thrownException instanceof ResponseStatusException rse) {
            assertEquals(409, rse.getStatusCode().value());
        }
        String message = getExceptionMessage(thrownException);
        assertTrue(message.contains(expectedMsg), "Expected message to contain: '" + expectedMsg + "' but was: '" + message + "'");
        verify(userRepository, never()).save(any());
    }

    @Then("the action should fail with a bad request error {string}")
    public void verify_bad_request_error(String expectedMsg) {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        if (thrownException instanceof ResponseStatusException rse) {
            assertEquals(400, rse.getStatusCode().value());
        }
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