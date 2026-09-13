package orange.smart_hire.bdd.steps.superadmin;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import orange.smart_hire.dto.AuditLogResponse;
import orange.smart_hire.dto.PlatformStatsResponse;
import orange.smart_hire.enums.PostingStatus;
import orange.smart_hire.enums.UserRole;
import orange.smart_hire.model.Posting;
import orange.smart_hire.model.User;
import orange.smart_hire.repository.ApplicationRepository;
import orange.smart_hire.repository.PostingRepository;
import orange.smart_hire.repository.UserRepository;
import orange.smart_hire.service.AuditLogService;
import orange.smart_hire.service.EmailService;
import orange.smart_hire.service.SuperAdminService;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class SuperAdminDashboardSteps {

    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final PostingRepository postingRepository = Mockito.mock(PostingRepository.class);
    private final ApplicationRepository applicationRepository = Mockito.mock(ApplicationRepository.class);
    private final AuditLogService auditLogService = Mockito.mock(AuditLogService.class);

    private final SuperAdminService superAdminService = new SuperAdminService(
            userRepository, Mockito.mock(PasswordEncoder.class), Mockito.mock(EmailService.class),
            auditLogService, postingRepository, applicationRepository
    );

    private PlatformStatsResponse statsResponse;
    private Page<AuditLogResponse> auditLogPage;

    @Before
    public void setUp() {
        statsResponse = null;
        auditLogPage = null;
        Mockito.reset(userRepository, postingRepository, applicationRepository, auditLogService);
    }

    @Given("the platform has {long} total users")
    public void setup_total_users(long count) {
        when(userRepository.count()).thenReturn(count);
    }

    @Given("{int} of them are active staff members and {int} are inactive staff members")
    public void setup_staff_counts(int activeCount, int inactiveCount) {
        List<User> mockStaff = new java.util.ArrayList<>();
        for (int i = 0; i < activeCount; i++) mockStaff.add(createUser(true));
        for (int i = 0; i < inactiveCount; i++) mockStaff.add(createUser(false));

        when(userRepository.findByRoleIn(List.of(UserRole.HR_MANAGER, UserRole.INTERVIEWER)))
                .thenReturn(mockStaff);
    }

    private User createUser(boolean isActive) {
        User u = new User();
        u.setRole(UserRole.HR_MANAGER);
        u.setActive(isActive);
        return u;
    }

    @Given("the platform has {long} total job postings, {int} of which are published")
    public void setup_postings(long total, int publishedCount) {
        when(postingRepository.count()).thenReturn(total);
        when(postingRepository.findByStatus(PostingStatus.PUBLISHED))
                .thenReturn(Collections.nCopies(publishedCount, new Posting()));
    }

    @Given("there are {long} total applications")
    public void setup_applications(long count) {
        when(applicationRepository.count()).thenReturn(count);
    }

    @When("the Super Admin requests platform stats")
    public void request_platform_stats() {
        statsResponse = superAdminService.getPlatformStats();
    }

    @Then("the stats should show {long} total users")
    public void verify_total_users(long expected) {
        assertNotNull(statsResponse);
        assertEquals(expected, statsResponse.getTotalUsers());
    }

    @Then("the stats should show {long} active and {long} inactive staff")
    public void verify_staff_stats(long active, long inactive) {
        assertEquals(active, statsResponse.getActiveStaff());
        assertEquals(inactive, statsResponse.getInactiveStaff());
    }

    @Then("the stats should show {long} total postings and {int} published postings")
    public void verify_posting_stats(long total, int published) {
        assertEquals(total, statsResponse.getTotalPostings());
        assertEquals(published, statsResponse.getPublishedPostings());
    }

    @Then("the stats should show {long} total applications")
    public void verify_application_stats(long total) {
        assertEquals(total, statsResponse.getTotalApplications());
    }

    @Given("the system has recorded audit logs for various actions")
    public void mock_audit_logs() {
        List<AuditLogResponse> logs = List.of(
                Mockito.mock(AuditLogResponse.class),
                Mockito.mock(AuditLogResponse.class)
        );
        when(auditLogService.findAll(any(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(logs));
    }

    @When("the Super Admin requests page {int} of audit logs with size {int}")
    public void request_audit_logs(int page, int size) {
        auditLogPage = auditLogService.findAll(null, PageRequest.of(page, size));
    }

    @Then("a paginated list of audit logs should be returned")
    public void verify_audit_logs_returned() {
        assertNotNull(auditLogPage);
        assertEquals(2, auditLogPage.getContent().size());
    }
}