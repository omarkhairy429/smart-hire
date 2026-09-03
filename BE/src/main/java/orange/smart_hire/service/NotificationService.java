package orange.smart_hire.service;



import orange.smart_hire.dto.NotificationResponse;
import orange.smart_hire.enums.NotificationType;
import orange.smart_hire.model.Notification;
import orange.smart_hire.model.User;
import orange.smart_hire.repository.NotificationRepository;
import orange.smart_hire.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public NotificationResponse sendNotification(
            UUID recipientId,
            NotificationType type,
            String title,
            String message,
            UUID relatedEntityId
    ){
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND, "Recipient not found"
                ));
        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setRelatedEntityId(relatedEntityId);
        notification.setRead(false);

        Notification saved = notificationRepository.save(notification);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications(UUID recipientId) {
       return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(recipientId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public void markAsRead(UUID notificationId, UUID recipientId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND, "Notification not found"
                ));
        if(!notification.getRecipient().getId().equals(recipientId)){
            throw new ResponseStatusException(NOT_FOUND, "Notification not found");
        }
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public void markAllAsRead(UUID recipientId) {
        List<Notification> notifications = notificationRepository.findByRecipientIdOrderByCreatedAtDesc(recipientId);
        notifications.forEach(notification -> notification.setRead(true));

        notificationRepository.saveAll(notifications);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(UUID recipientId) {
        return notificationRepository.countByRecipientIdAndReadFalse(recipientId);
    }


    private NotificationResponse mapToResponse(
            Notification notification
    ) {

        NotificationResponse response =
                new NotificationResponse();

        response.setId(notification.getId());
        response.setType(notification.getType());
        response.setTitle(notification.getTitle());
        response.setMessage(notification.getMessage());
        response.setRelatedEntityId(
                notification.getRelatedEntityId()
        );
        response.setRead(notification.isRead());
        response.setCreatedAt(notification.getCreatedAt());

        return response;
    }
}
