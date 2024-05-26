package uns.ac.rs.model;

import jakarta.persistence.*;
import lombok.Data;
import uns.ac.rs.dto.AccommodationReviewDTO;
import uns.ac.rs.dto.HostReviewDTO;

@Entity
@Table(name = "accommodation_review")
@Data
public class AccommodationReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "guest_email")
    private String guestEmail;

    @Column(name = "accommodation_name")
    private String accommodationName;

    @Column(name = "timestamp")
    private Long timestamp;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "deleted")
    private boolean deleted;

    public AccommodationReview() {

    }

    public AccommodationReview(AccommodationReviewDTO accommodationReviewDTO, String guestEmail) {
        this.guestEmail = guestEmail;
        this.accommodationName = accommodationReviewDTO.getAccommodationName();
        this.timestamp = accommodationReviewDTO.getTimestamp();
        this.rating = accommodationReviewDTO.getRating();
        this.deleted = false;
    }

}
