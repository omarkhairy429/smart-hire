package orange.smart_hire.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import orange.smart_hire.enums.UserRole;

@Getter
@Setter
public class CreateStaffRequest {
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String rawPassword;

    @NotNull
    private UserRole role;
}