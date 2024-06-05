package uns.ac.rs.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;
import uns.ac.rs.model.AccommodationReview;

@Data
@RegisterForReflection
public class AccommodationReviewDTO {

    private Long accommodationId;
    private String hostEmail;
    private Long timestamp;
    private Integer rating;
    private boolean deleted;

    public AccommodationReviewDTO() {
        this.accommodationId = null;
        this.hostEmail = null;
        this.timestamp = null;
        this.rating = null;
        this.deleted = false;
    }

    public AccommodationReviewDTO(AccommodationReview accommodationReview) {
        this.accommodationId = accommodationReview.getAccommodationId();
        this.hostEmail = accommodationReview.getHostEmail();
        this.timestamp = accommodationReview.getTimestamp();
        this.rating = accommodationReview.getRating();
        this.deleted = accommodationReview.isDeleted();
    }
}
