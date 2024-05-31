package uns.ac.rs.dto.response;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;
import uns.ac.rs.model.User;

@Data
@RegisterForReflection
public class UserResponseDTO {
    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private String city;
    private String country;
    private boolean automaticReservationAcceptance;

    public UserResponseDTO(User user) {
        email = user.getEmail();
        username = user.getUsername();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        city = user.getCity();
        country = user.getCountry();
        automaticReservationAcceptance = user.isAutomaticReservationAcceptance();
    }
}
