package orange.smart_hire.service;

import lombok.RequiredArgsConstructor;
import orange.smart_hire.dto.PlatformStatsResponse;
import orange.smart_hire.dto.RegisterRequest;
import orange.smart_hire.dto.StaffResponse;
import orange.smart_hire.enums.PostingStatus;
import orange.smart_hire.enums.UserRole;
import orange.smart_hire.model.User;
import orange.smart_hire.repository.ApplicationRepository;
import orange.smart_hire.repository.PostingRepository;
import orange.smart_hire.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SuperAdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AuditLogService auditLogService;
    private final PostingRepository postingRepository;
    private final ApplicationRepository applicationRepository;


    @Transactional
    public User createStaffMember(RegisterRequest request) {
        if (request.getRole() != UserRole.HR_MANAGER && request.getRole() != UserRole.INTERVIEWER) {
            throw new IllegalArgumentException("Super Admin can only create HR Managers or Interviewers");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("User with this email already exists: " + request.getEmail());
        }

        // HR Managers and Interviewers must belong to a company
        if (request.getCompanyName() == null || request.getCompanyName().isBlank()) {
            throw new IllegalArgumentException("A company name is required for HR Managers and Interviewers");
        }

        User staffMember = new User();
        staffMember.setFirstName(request.getFirstName());
        staffMember.setLastName(request.getLastName());
        staffMember.setEmail(request.getEmail());
        staffMember.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        staffMember.setRole(request.getRole());
        staffMember.setCompanyName(request.getCompanyName().trim());
        staffMember.setActive(true);

        emailService.sendEmail(
                request.getEmail(),
                "Welcome to SmartHire – Your Account Credentials",
                "Welcome to SmartHire!\n\n" +
                        "Your account has been created successfully.\n\n" +
                        "Here are your temporary login credentials:\n\n" +
                        "Email: " + request.getEmail() + "\n" +
                        "Temporary Password: " + request.getPassword() + "\n\n" +
                        "Please log in using these credentials and change your password after your first login.\n\n" +
                        "Best regards,\n" +
                        "SmartHire Team"
        );


        return userRepository.save(staffMember);
    }

    @Transactional(readOnly = true)
    public List<StaffResponse> getAllStaff() {
        List<UserRole> staffRoles = List.of(UserRole.HR_MANAGER, UserRole.INTERVIEWER);
        return userRepository.findByRoleIn(staffRoles)
                .stream()
                .map(StaffResponse::fromEntity)
                .toList();
    }

    @Transactional
    public StaffResponse deactivateStaff(UUID id) {
        User staff = findStaffOrThrow(id);

        if (!staff.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "This account is already deactivated");
        }

        staff.setActive(false);
        userRepository.save(staff);

        auditLogService.log("STAFF_DEACTIVATED", "User", staff.getId(),
                Map.of("email", staff.getEmail(), "role", staff.getRole().name()));

        return StaffResponse.fromEntity(staff);
    }

    @Transactional
    public StaffResponse reactivateStaff(UUID id) {
        User staff = findStaffOrThrow(id);

        if (staff.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "This account is already active");
        }

        staff.setActive(true);
        userRepository.save(staff);

        auditLogService.log("STAFF_REACTIVATED", "User", staff.getId(),
                Map.of("email", staff.getEmail(), "role", staff.getRole().name()));

        return StaffResponse.fromEntity(staff);
    }

    private User findStaffOrThrow(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        if (user.getRole() != UserRole.HR_MANAGER && user.getRole() != UserRole.INTERVIEWER) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Only HR Managers and Interviewers can be deactivated");
        }

        return user;
    }
    @Transactional(readOnly = true)
    public PlatformStatsResponse getPlatformStats() {
        List<UserRole> staffRoles = List.of(UserRole.HR_MANAGER, UserRole.INTERVIEWER);
        List<User> staff = userRepository.findByRoleIn(staffRoles);

        return PlatformStatsResponse.builder()
                .totalUsers(userRepository.count())
                .activeStaff(staff.stream().filter(User::isActive).count())
                .inactiveStaff(staff.stream().filter(u -> !u.isActive()).count())
                .totalPostings(postingRepository.count())
                .publishedPostings(postingRepository.findByStatus(PostingStatus.PUBLISHED).size())
                .totalApplications(applicationRepository.count())
                .build();
    }
}