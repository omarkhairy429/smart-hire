package orange.smart_hire.service;

import lombok.RequiredArgsConstructor;
import orange.smart_hire.dto.AuditLogResponse;
import orange.smart_hire.model.AuditLog;
import orange.smart_hire.model.User;
import orange.smart_hire.repository.AuditLogRepository;
import orange.smart_hire.repository.UserRepository;
import orange.smart_hire.utils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    /** Records an action performed by the currently authenticated user. */
    @Transactional
    public void log(String action, String entityType, UUID entityId, Map<String, Object> details) {
        AuditLog entry = new AuditLog();
        entry.setActorId(SecurityUtils.getCurrentUserId());
        entry.setAction(action);
        entry.setEntityType(entityType);
        entry.setEntityId(entityId);
        entry.setDetails(details);
        auditLogRepository.save(entry);
    }

    @Transactional(readOnly = true)
    public Page<AuditLogResponse> findAll(String action, Pageable pageable) {
        Page<AuditLog> logs = (action == null || action.isBlank())
                ? auditLogRepository.findAllByOrderByCreatedAtDesc(pageable)
                : auditLogRepository.findByActionContainingIgnoreCaseOrderByCreatedAtDesc(action, pageable);

        List<UUID> actorIds = logs.getContent().stream()
                .map(AuditLog::getActorId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<UUID, String> namesById = userRepository.findAllById(actorIds).stream()
                .collect(Collectors.toMap(
                        User::getId,
                        user -> user.getFirstName() + " " + user.getLastName()));

        return logs.map(entry -> AuditLogResponse.fromEntity(
                entry,
                entry.getActorId() == null ? "System" : namesById.getOrDefault(entry.getActorId(), "Unknown user")));
    }
}