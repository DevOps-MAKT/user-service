package uns.ac.rs.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

@Data
@RegisterForReflection
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
