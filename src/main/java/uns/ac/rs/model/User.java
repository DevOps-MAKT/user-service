package uns.ac.rs.model;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;
import jakarta.persistence.*;
import lombok.Data;
import uns.ac.rs.dto.request.UserRequestDTO;

@Entity
@UserDefinition
@Table(name = "users")
@Data
public class User  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String email;
    @Username
    @Column(unique = true)
    private String username;
    @Password
    private String password; // By default, it uses bcrypt-hashed passwords
    @Roles
    private String role; // comma-separated list of roles added to the target principal representation attributes.
    private String firstName;
    private String lastName;
    private String city;
    private String country;
    private boolean automaticReservationAcceptance;
    private int noCancellations;
    private boolean active;
    private boolean reservationRequestedNotificationsActive;
    private boolean reservationCancelledNotificationsActive;
    private boolean hostRatedNotificationsActive;
    private boolean accommodationRatedNotificationsActive;
    private boolean reservationRequestAnsweredActive;

    public User(UserRequestDTO userRequestDTO) {
        email = userRequestDTO.getEmail();
        username = userRequestDTO.getUsername();
        password = BcryptUtil.bcryptHash(userRequestDTO.getPassword());
        role = userRequestDTO.getRole();
        firstName = userRequestDTO.getFirstName();
        lastName = userRequestDTO.getLastName();
        city = userRequestDTO.getCity();
        country = userRequestDTO.getCountry();
        automaticReservationAcceptance = false;
        noCancellations = 0;
        active = true;
        reservationRequestedNotificationsActive = false;
        reservationCancelledNotificationsActive = false;
        hostRatedNotificationsActive = false;
        accommodationRatedNotificationsActive = false;
        reservationRequestAnsweredActive = false;
    }

    public User() {

    }

    public User(String email, String username, String password, String role, String firstName, String lastName, String city, String country) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.city = city;
        this.country = country;
        automaticReservationAcceptance = false;
        noCancellations = 0;
        active = true;
    }
}
