package uns.ac.rs.dto;

import lombok.Data;

import java.util.List;

@Data
public class HostReviewInfoDTO {
    private List<HostReviewDTO> reviews;
    private float avgRating;

    public HostReviewInfoDTO() {

    }

    public HostReviewInfoDTO(List<HostReviewDTO> reviews, float avgRating) {
        this.reviews = reviews;
        this.avgRating = avgRating;
    }
}
