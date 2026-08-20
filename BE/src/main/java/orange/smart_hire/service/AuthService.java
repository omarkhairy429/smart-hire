package orange.smart_hire.service;

import orange.smart_hire.dto.AuthResponse;
import orange.smart_hire.dto.LoginRequest;
import orange.smart_hire.dto.RegisterRequest;
import orange.smart_hire.enums.UserRole;
import orange.smart_hire.model.User;
import orange.smart_hire.repository.UserRepository;
import orange.smart_hire.model.CustomUserDetails;
import orange.smart_hire.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        /* HR and Interviewer can't register themselves */
        user.setRole(UserRole.CANDIDATE);
        user.setActive(true);

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser.getId());

        return new AuthResponse(
                token,
                savedUser.getRole().name(),
                savedUser.getEmail(),
                savedUser.getFirstName()
        );
    }

    public AuthResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        User user = userDetails.getUser();
        String token = jwtService.generateToken(user.getId());

        return new AuthResponse(
                token,
                user.getRole().name(),
                user.getEmail(),
                user.getFirstName()
        );
    }
}