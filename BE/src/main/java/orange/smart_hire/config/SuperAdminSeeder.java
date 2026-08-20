package orange.smart_hire.config;

import lombok.RequiredArgsConstructor;
import orange.smart_hire.enums.UserRole;
import orange.smart_hire.model.User;
import orange.smart_hire.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
// import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SuperAdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SuperAdminSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        String adminEmail = "admin@smarthire.orange";

        if (!userRepository.existsByEmail(adminEmail)) {
            User superAdmin = new User();
            superAdmin.setFirstName("Super");
            superAdmin.setLastName("Admin");
            superAdmin.setEmail(adminEmail);
            // PasswordEncoder.encode
            superAdmin.setPasswordHash(passwordEncoder.encode("Admin@Secure123"));
            superAdmin.setRole(UserRole.SUPER_ADMIN);
            superAdmin.setActive(true);
            userRepository.save(superAdmin);
        }
    }
}