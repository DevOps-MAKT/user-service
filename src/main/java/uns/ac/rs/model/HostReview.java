package uns.ac.rs.model;

import jakarta.persistence.*;
import lombok.Data;
import uns.ac.rs.dto.HostReviewDTO;

@Entity
@Table(name = "host_reviews")
@Data
public class HostReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "guest_email")
    private String guestEmail;

    @Column(name = "host_email")
    private String hostEmail;

    @Column(name = "timestamp")
    private Long timestamp;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "deleted")
    private boolean deleted;

    public HostReview() {

    }

    public HostReview(HostReviewDTO hostReviewDTO, String guestEmail) {
        this.guestEmail = guestEmail;
        this.hostEmail = hostReviewDTO.getHostEmail();
        this.timestamp = hostReviewDTO.getTimestamp();
        this.rating = hostReviewDTO.getRating();
        this.deleted = false;
    }

}
