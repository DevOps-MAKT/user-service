package uns.ac.rs.model;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
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
    @ManyToOne
    @JoinColumn(name="location_id")
    private Location location;

    public User(UserRequestDTO userRequestDTO, Location location) {
        email = userRequestDTO.getEmail();
        username = userRequestDTO.getUsername();
        password = BcryptUtil.bcryptHash(userRequestDTO.getPassword());
        role = userRequestDTO.getRole();
        firstName = userRequestDTO.getFirstName();
        lastName = userRequestDTO.getLastName();
        this.location = location;
    }

    public User() {

    }

    public User(String email, String username, String password, String role, String firstName, String lastName, Location location) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.location = location;
    }
}
