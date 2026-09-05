package orange.smart_hire.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlatformStatsResponse {
    private long totalUsers;
    private long activeStaff;
    private long inactiveStaff;
    private long totalPostings;
    private long publishedPostings;
    private long totalApplications;
}