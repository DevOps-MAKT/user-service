package uns.ac.rs.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.springframework.stereotype.Repository;
import uns.ac.rs.model.User;

import java.util.Optional;

@Repository
public class UserRepository implements PanacheRepository<User> {
    public Optional<User> findByUsername(String username) {
        return find("username = ?1 and active = ?2", username, true).firstResultOptional();
    }

    public User findByEmail(String email) {
        return find("email = ?1 and active = ?2", email, true).firstResult();
    }

    public User findByEmailAndRole(String email, String role) {
        return find("email = ?1 and role = ?2 and active= ?3", email, role, true).firstResult();
    }

}
