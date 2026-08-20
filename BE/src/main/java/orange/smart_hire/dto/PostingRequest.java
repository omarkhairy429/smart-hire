package orange.smart_hire.dto;

import orange.smart_hire.enums.LocationType;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class PostingRequest {

    private UUID hrManagerId;
    private String title;
    private String description;
    private List<String> skillsRequired;
    private LocationType locationType;
    private String location;
    private LocalDate deadline;

    public UUID getHrManagerId() {
        return hrManagerId;
    }

    public void setHrManagerId(UUID hrManagerId) {
        this.hrManagerId = hrManagerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getSkillsRequired() {
        return skillsRequired;
    }

    public void setSkillsRequired(List<String> skillsRequired) {
        this.skillsRequired = skillsRequired;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }
}