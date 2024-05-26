package uns.ac.rs.dto;

import lombok.Data;

import java.util.List;

@Data
public class AccommodationReviewInfoDTO {
    private List<AccommodationReviewDTO> reviews;
    private float avgRating;

    public AccommodationReviewInfoDTO() {

    }
}
