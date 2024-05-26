package uns.ac.rs.dto;

import lombok.Data;

@Data
public class MinAccommodationDTO {

    private long id;
    private String name;

    public MinAccommodationDTO() {

    }

    public MinAccommodationDTO(long id, String name) {
        this.id = id;
        this.name = name;
    }
}
