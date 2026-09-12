package orange.smart_hire.service;

import orange.smart_hire.dto.AuthResponse;
import orange.smart_hire.dto.LoginRequest;
import orange.smart_hire.dto.RegisterRequest;
import orange.smart_hire.enums.UserRole;
import orange.smart_hire.exception.DuplicateResourceException;
import orange.smart_hire.exception.ResourceNotFoundException;
import orange.smart_hire.exception.TokenException;
import orange.smart_hire.model.User;
import orange.smart_hire.repository.UserRepository;
import orange.smart_hire.model.CustomUserDetails;
import orange.smart_hire.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import orange.smart_hire.model.PasswordResetToken;
import orange.smart_hire.repository.PasswordResetTokenRepository;
import orange.smart_hire.dto.ForgotPasswordRequest;
import orange.smart_hire.dto.ResetPasswordRequest;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            PasswordResetTokenRepository passwordResetTokenRepository,
            EmailService emailService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailService = emailService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email is already in use");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
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

    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiresAt(LocalDateTime.now().plusHours(24));
        resetToken.setUsed(false);

        passwordResetTokenRepository.save(resetToken);

        String resetLink =
                "http://localhost:4200/reset-password?token=" + token;

        emailService.sendEmail(
                user.getEmail(),
                "Reset Your SmartHire Password",
                "Hello " + user.getFirstName() + ",\n\n" +
                        "You requested to reset your SmartHire password.\n\n" +
                        "Please click the link below to reset your password:\n\n" +
                        resetLink + "\n\n" +
                        "This link will expire in 24 hours.\n\n" +
                        "If you did not request a password reset, please ignore this email.\n\n" +
                        "Best regards,\n" +
                        "SmartHire Team"
        );
    }

    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository
                .findByToken(request.getToken())
                .orElseThrow(() -> new TokenException("Invalid reset token"));

        if (resetToken.isUsed()) {
            throw new TokenException("Reset token has already been used");
        }

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenException("Reset token has expired");
        }

        User user = resetToken.getUser();

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }
}