package orange.smart_hire.dto;

import lombok.Getter;
import lombok.Setter;
import orange.smart_hire.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class NotificationResponse {
    private UUID id;
    private NotificationType type;
    private String title;
    private String message;
    private UUID relatedEntityId;
    private boolean read;
    private LocalDateTime createdAt;
}
