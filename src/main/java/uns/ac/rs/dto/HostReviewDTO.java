package uns.ac.rs.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;
import uns.ac.rs.model.HostReview;

@Data
@RegisterForReflection
public class HostReviewDTO {

    private String hostEmail;
    private Long timestamp;
    private Integer rating;
    private boolean deleted;

    public HostReviewDTO() {
        this.hostEmail = null;
        this.timestamp = null;
        this.rating = null;
        this.deleted = false;
    }

    public HostReviewDTO(HostReview hostReview) {
        this.hostEmail = hostReview.getHostEmail();
        this.timestamp = hostReview.getTimestamp();
        this.rating = hostReview.getRating();
        this.deleted = hostReview.isDeleted();
    }
}
