package uns.ac.rs.dto;

import lombok.Data;
import uns.ac.rs.model.Location;

@Data
public class LocationDTO {
    private String country;
    private String city;

    public LocationDTO(Location location) {
        country = location.getCountry();
        city = location.getCity();
    }

    public LocationDTO() {

    }
}
