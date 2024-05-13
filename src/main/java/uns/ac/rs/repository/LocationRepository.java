package uns.ac.rs.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.springframework.stereotype.Repository;
import uns.ac.rs.model.Location;

@Repository
public class LocationRepository implements PanacheRepository<Location> {

    public Location findByCityAndCountry(String city, String country) {
        return find("city = ?1 and country = ?2", city, country).firstResult();
    }
}
