package uns.ac.rs.dto.response;

import lombok.Data;
import uns.ac.rs.dto.LocationDTO;
import uns.ac.rs.model.User;

@Data
public class UserResponseDTO {
    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private LocationDTO location;

    public UserResponseDTO(User user) {
        email = user.getEmail();
        username = user.getUsername();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        location = new LocationDTO(user.getLocation());
    }
}
