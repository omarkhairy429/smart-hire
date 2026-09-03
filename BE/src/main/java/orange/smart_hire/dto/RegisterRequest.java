package orange.smart_hire.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import orange.smart_hire.enums.UserRole;

@Getter
@Setter
public class RegisterRequest {
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotNull
    private UserRole role;

    /**
     * Company name for HR_MANAGER and INTERVIEWER accounts.
     * Optional for CANDIDATE (self-register). SUPER_ADMIN never has a company.
     */
    private String companyName;
}