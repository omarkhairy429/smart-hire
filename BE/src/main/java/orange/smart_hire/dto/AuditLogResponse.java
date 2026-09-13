package orange.smart_hire.dto;

import lombok.Builder;
import lombok.Getter;
import orange.smart_hire.model.AuditLog;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@Builder
public class AuditLogResponse {
    private UUID id;
    private UUID actorId;
    private String actorName;
    private String action;
    private String entityType;
    private UUID entityId;
    private Map<String, Object> details;
    private LocalDateTime createdAt;

    public static AuditLogResponse fromEntity(AuditLog log, String actorName) {
        return AuditLogResponse.builder()
                .id(log.getId())
                .actorId(log.getActorId())
                .actorName(actorName)
                .action(log.getAction())
                .entityType(log.getEntityType())
                .entityId(log.getEntityId())
                .details(log.getDetails())
                .createdAt(log.getCreatedAt())
                .build();
    }
}