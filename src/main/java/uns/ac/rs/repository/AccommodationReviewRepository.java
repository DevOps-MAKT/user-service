package uns.ac.rs.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.springframework.stereotype.Repository;
import uns.ac.rs.model.AccommodationReview;
import uns.ac.rs.model.HostReview;

import java.util.List;
import java.util.Optional;

@Repository
public class AccommodationReviewRepository implements PanacheRepository<AccommodationReview> {

    public Optional<AccommodationReview> findByGuestEmailAndAccommodationName(String guestEmail, String accommodationName) {
        return find("guestEmail = ?1 and accommodationName = ?2 and deleted = ?3", guestEmail, accommodationName, false)
                .firstResultOptional();
    }

    public Optional<List<AccommodationReview>> findByAccommodationName(String accommodationName) {
        return Optional.ofNullable(list("accommodationName = ?1 and deleted = ?2", accommodationName, false));
    }
}
