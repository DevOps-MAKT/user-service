package uns.ac.rs.dto;

import lombok.Data;
import uns.ac.rs.model.HostReview;

@Data
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
