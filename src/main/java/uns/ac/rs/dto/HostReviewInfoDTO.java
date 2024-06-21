package uns.ac.rs.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

import java.util.List;

@Data
@RegisterForReflection
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
