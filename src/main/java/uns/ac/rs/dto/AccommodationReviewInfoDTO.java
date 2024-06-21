package uns.ac.rs.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

import java.util.List;

@Data
@RegisterForReflection
public class AccommodationReviewInfoDTO {
    private List<AccommodationReviewDTO> reviews;
    private float avgRating;
}
