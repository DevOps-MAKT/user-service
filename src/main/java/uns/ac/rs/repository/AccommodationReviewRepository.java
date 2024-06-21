package uns.ac.rs.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.springframework.stereotype.Repository;
import uns.ac.rs.model.AccommodationReview;

import java.util.List;
import java.util.Optional;

@Repository
public class AccommodationReviewRepository implements PanacheRepository<AccommodationReview> {

    public Optional<AccommodationReview> findByGuestEmailAndAccommodationId(String guestEmail, Long accommodationId) {
        return find("guestEmail = ?1 and accommodationId = ?2 and deleted = ?3", guestEmail, accommodationId, false)
                .firstResultOptional();
    }

    public Optional<List<AccommodationReview>> findByAccommodationId(Long accommodationId) {
        return Optional.ofNullable(list("accommodationId = ?1 and deleted = ?2", accommodationId, false));
    }

    public Optional<List<AccommodationReview>> findByGuestEmailId(String guestEmail) {
        return Optional.ofNullable(list("guestEmail = ?1 and deleted = ?2", guestEmail, false));
    }
}
