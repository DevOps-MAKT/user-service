package uns.ac.rs.dto.request;

import lombok.Data;
import uns.ac.rs.dto.LocationDTO;

@Data
public class UserRequestDTO {
    private String email;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String role;
    private LocationDTO location;

    public UserRequestDTO(String email, String username, String password, String firstName, String lastName, String role, LocationDTO location) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.location = location;
    }
}
