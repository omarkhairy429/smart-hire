package orange.smart_hire.service;

import lombok.RequiredArgsConstructor;
import orange.smart_hire.dto.RegisterRequest;
import orange.smart_hire.dto.StaffResponse;
import orange.smart_hire.enums.UserRole;
import orange.smart_hire.model.User;
import orange.smart_hire.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SuperAdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User createStaffMember(RegisterRequest request) {
        if (request.getRole() != UserRole.HR_MANAGER && request.getRole() != UserRole.INTERVIEWER) {
            throw new IllegalArgumentException("Super Admin can only create HR Managers or Interviewers");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("User with this email already exists: " + request.getEmail());
        }

        User staffMember = new User();
        staffMember.setFirstName(request.getFirstName());
        staffMember.setLastName(request.getLastName());
        staffMember.setEmail(request.getEmail());
        staffMember.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        staffMember.setRole(request.getRole());
        staffMember.setActive(true);

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
}