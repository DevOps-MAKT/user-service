package uns.ac.rs.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

import java.util.List;

@Data
@RegisterForReflection
public class GuestReviewsInfoDTO {
    private List<AccommodationReviewDTO> accommodationReviews;
    private List<HostReviewDTO> hostReviews;
}
