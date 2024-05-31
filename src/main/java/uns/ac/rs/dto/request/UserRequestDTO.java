package uns.ac.rs.dto.request;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

@Data
@RegisterForReflection
public class UserRequestDTO {
    private String email;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String role;
    private String city;
    private String country;

    public UserRequestDTO(String email, String username, String password, String firstName, String lastName, String role, String city, String country) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.city = city;
        this.country = country;
    }
}
