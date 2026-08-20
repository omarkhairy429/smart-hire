package orange.smart_hire.dto;

import lombok.Builder;
import lombok.Getter;
import orange.smart_hire.enums.UserRole;
import orange.smart_hire.model.User;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class StaffResponse {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private UserRole role;
    private boolean isActive;
    private LocalDateTime createdAt;

    public static StaffResponse fromEntity(User user) {
        return StaffResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}