package orange.smart_hire.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

public class CandidateNoteDto {

    @Data
    public static class Request {
        private UUID candidateId;
        private String content;
    }

    @Data
    public static class Response {
        private UUID id;
        private String content;
        private String authorName;
        private LocalDateTime createdAt;
    }
}
