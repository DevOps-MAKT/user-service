package uns.ac.rs.dto;

import lombok.Data;
import uns.ac.rs.model.AccommodationReview;

@Data
public class AccommodationReviewDTO {

    private String accommodationName;
    private Long timestamp;
    private Integer rating;
    private boolean deleted;

    public AccommodationReviewDTO() {
        this.accommodationName = null;
        this.timestamp = null;
        this.rating = null;
        this.deleted = false;
    }

    public AccommodationReviewDTO(AccommodationReview accommodationReview) {
        this.accommodationName = accommodationReview.getAccommodationName();
        this.timestamp = accommodationReview.getTimestamp();
        this.rating = accommodationReview.getRating();
        this.deleted = accommodationReview.isDeleted();
    }
}
