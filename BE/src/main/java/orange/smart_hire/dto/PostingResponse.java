package orange.smart_hire.dto;

import orange.smart_hire.enums.LocationType;
import orange.smart_hire.enums.PostingStatus;

import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class PostingResponse {

    private UUID id;
    private UUID hrManagerId;
    private String title;
    private String description;
    private List<String> skillsRequired;
    private LocationType locationType;
    private String location;
    private PostingStatus status;
    private LocalDate deadline;
    private Instant createdAt;
    private Instant updatedAt;

    public PostingResponse() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public PostingStatus getStatus() {
        return status;
    }

    public void setStatus(PostingStatus status) {
        this.status = status;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}