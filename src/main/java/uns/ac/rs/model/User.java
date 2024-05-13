package uns.ac.rs.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@UserDefinition
@Table(name = "users")
@Data
public class User  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    @Username
    private String username;
    @Password
    private String password; // By default, it uses bcrypt-hashed passwords
    @Roles
    private String role; // comma-separated list of roles added to the target principal representation attributes.
    private String firstName;
    private String lastName;
}
