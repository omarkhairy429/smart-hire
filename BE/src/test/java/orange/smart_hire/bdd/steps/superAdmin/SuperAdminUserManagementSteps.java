package orange.smart_hire.bdd.steps.superAdmin;


import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import orange.smart_hire.dto.RegisterRequest;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class SuperAdminUserManagementSteps {

    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
    private final EmailService emailService = Mockito.mock(EmailService.class);
    private final AuditLogService auditLogService = Mockito.mock(AuditLogService.class);
    private final PostingRepository postingRepository = Mockito.mock(PostingRepository.class);
    private final ApplicationRepository applicationRepository = Mockito.mock(ApplicationRepository.class);

    private final SuperAdminService superAdminService = new SuperAdminService(
            userRepository, passwordEncoder, emailService, auditLogService, postingRepository, applicationRepository
    );

    private User createdUser;
    private Exception thrownException;
    private List<StaffResponse> staffList;

    @Before
    public void setUp() {
        createdUser = null;
        thrownException = null;
        staffList = null;
        Mockito.reset(userRepository, passwordEncoder, emailService, auditLogService, postingRepository, applicationRepository);
    }

    @Given("an email {string} does not exist")
    public void email_does_not_exist(String email) {
        when(userRepository.existsByEmail(email)).thenReturn(false);
    }

    @Given("an email {string} already exists")
    public void email_already_exists(String email) {
        when(userRepository.existsByEmail(email)).thenReturn(true);
    }

    @When("the Super Admin creates a user with role {string}, email {string}, and company {string}")
    public void create_user(String roleStr, String email, String company) {
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("Mock");
        request.setLastName("User");
        request.setEmail(email);
        request.setPassword("StrongPass123");
        request.setRole(UserRole.valueOf(roleStr));
        request.setCompanyName(company);

        when(passwordEncoder.encode(any())).thenReturn("hashed_pass");
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId(UUID.randomUUID());
            return u;
        });

        try {
            createdUser = superAdminService.createStaffMember(request);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("the staff account should be created successfully")
    public void account_created() {
        assertNull(thrownException, "Expected successful creation, but got exception: " +
                (thrownException != null ? thrownException.getMessage() : ""));
        assertNotNull(createdUser);
    }

    @And("a welcome email should be sent to {string}")
    public void email_sent(String email) {
        verify(emailService, times(1)).sendEmail(eq(email), anyString(), anyString());
    }

    @Then("the user creation should fail with message {string}")
    public void creation_failed(String msg) {
        assertNotNull(thrownException, "Expected an exception to be thrown for validation failure, but it was null.");
        assertNotNull(thrownException.getMessage(), "Exception thrown, but message is null.");
        assertTrue(thrownException.getMessage().contains(msg),
                "Expected error message containing: '" + msg + "' but got: '" + thrownException.getMessage() + "'");
    }

    @Given("the system contains {int} {string} and {int} {string} accounts")
    public void system_contains_specific_staff(int count1, String role1, int count2, String role2) {
        List<User> mockStaff = new ArrayList<>();

        for (int i = 0; i < count1; i++) {
            User u = new User();
            u.setId(UUID.randomUUID());
            u.setFirstName("Name1_" + i);
            u.setLastName("Last");
            u.setEmail("test1_" + i + "@test.com");
            u.setRole(UserRole.valueOf(role1));
            u.setCompanyName("Company A");
            mockStaff.add(u);
        }

        for (int i = 0; i < count2; i++) {
            User u = new User();
            u.setId(UUID.randomUUID());
            u.setFirstName("Name2_" + i);
            u.setLastName("Last");
            u.setEmail("test2_" + i + "@test.com");
            u.setRole(UserRole.valueOf(role2));
            u.setCompanyName("Company B");
            mockStaff.add(u);
        }

        when(userRepository.findByRoleIn(anyList())).thenReturn(mockStaff);
    }

    @When("the Super Admin requests to view all staff")
    public void request_view_staff() {
        staffList = superAdminService.getAllStaff();
    }

    @Then("exactly {int} staff members should be returned")
    public void exactly_n_staff_returned(int expectedCount) {
        assertNotNull(staffList);
        assertEquals(expectedCount, staffList.size(), "The number of returned staff members is incorrect.");
    }

    @And("the returned list should only contain {string} and {string} roles")
    public void list_contains_only_specific_roles(String role1, String role2) {
        boolean onlyAllowedRoles = staffList.stream()
                .allMatch(staff -> String.valueOf(staff.getRole()).equals(role1) || String.valueOf(staff.getRole()).equals(role2));

        assertTrue(onlyAllowedRoles, "The returned list contains unauthorized roles!");
    }
}