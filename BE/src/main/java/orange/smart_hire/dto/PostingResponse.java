package orange.smart_hire.dto;

import lombok.Getter;
import lombok.Setter;
import orange.smart_hire.enums.EmploymentType;
import orange.smart_hire.enums.LocationType;
import orange.smart_hire.enums.PostingStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class PostingResponse {

    private UUID id;
    private String title;
    private String company;
    private String description;
    private List<String> skillsRequired;
    private LocationType locationType;
    private String location;
    private PostingStatus status;
    private LocalDate deadline;
    private String department;
    private EmploymentType employmentType;
    private Instant createdAt;
    private Instant updatedAt;

    public PostingResponse() {}

}