package orange.smart_hire.dto;

import lombok.Getter;
import lombok.Setter;
import orange.smart_hire.enums.EmploymentType;
import orange.smart_hire.enums.LocationType;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class PostingRequest {
    private String title;
    private String company;
    private String description;
    private List<String> skillsRequired;
    private LocationType locationType;
    private String location;
    private LocalDate deadline;
    private String department;
    private EmploymentType employmentType;

}