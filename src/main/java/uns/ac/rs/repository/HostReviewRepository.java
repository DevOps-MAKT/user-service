package uns.ac.rs.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.springframework.stereotype.Repository;
import uns.ac.rs.model.HostReview;

import java.util.List;
import java.util.Optional;

@Repository
public class HostReviewRepository implements PanacheRepository<HostReview> {

    public Optional<HostReview> findByGuestEmailAndHostEmail(String guestEmail, String hostEmail) {
        return find("guestEmail = ?1 and hostEmail = ?2 and deleted = ?3", guestEmail, hostEmail, false)
                .firstResultOptional();
    }

    public Optional<List<HostReview>> findByHostEmail(String hostEmail) {
        return Optional.ofNullable(list("hostEmail = ?1 and deleted = ?2", hostEmail, false));
    }
}
