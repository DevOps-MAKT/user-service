package uns.ac.rs.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.springframework.stereotype.Repository;
import uns.ac.rs.model.User;

@Repository
public class UserRepository implements PanacheRepository<User> {
    public User findByUsername(String username) {
        return find("username = ?1", username).firstResult();
    }

}
