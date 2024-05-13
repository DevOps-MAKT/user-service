package uns.ac.rs.model;

import jakarta.persistence.*;
import lombok.Data;
import uns.ac.rs.dto.LocationDTO;

@Entity
@Data
@Table(name = "locations")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String city;
    private String country;

    public Location(LocationDTO locationDTO) {
        city = locationDTO.getCity();
        country = locationDTO.getCountry();
    }

    public Location(String city, String country) {
        this.city = city;
        this.country = country;
    }

    public Location() {

    }
}
