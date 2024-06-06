package uns.ac.rs.model;

import jakarta.persistence.*;
import lombok.Data;
import uns.ac.rs.dto.AccommodationReviewDTO;

import java.time.Instant;

@Entity
@Table(name = "accommodation_review")
@Data
public class AccommodationReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "guest_email")
    private String guestEmail;

    @Column(name = "host_email")
    private String hostEmail;

    @Column(name = "accommodation_id")
    private Long accommodationId;

    @Column(name = "timestamp")
    private Long timestamp;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "deleted")
    private boolean deleted;

    public AccommodationReview() {

    }

    public AccommodationReview(String guestEmail, String hostEmail, Long accommodationId, Long timestamp, Integer rating, boolean deleted) {
        this.guestEmail = guestEmail;
        this.hostEmail = hostEmail;
        this.accommodationId = accommodationId;
        this.timestamp = timestamp;
        this.rating = rating;
        this.deleted = deleted;
    }

    public AccommodationReview(AccommodationReviewDTO accommodationReviewDTO, String guestEmail) {
        this.guestEmail = guestEmail;
        this.hostEmail = accommodationReviewDTO.getHostEmail();
        this.accommodationId = accommodationReviewDTO.getAccommodationId();
        this.timestamp = Instant.now().toEpochMilli();
        this.rating = accommodationReviewDTO.getRating();
        this.deleted = false;
    }

}
