package cs.job.recruit.api.input;

import lombok.Data;

@Data
public class InterviewRequest {
    private String dateTime; // ISO 8601 string or custom format
    private String location;
    private String notes;
}
