package uns.ac.rs.service;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uns.ac.rs.dto.request.UserRequestDTO;
import uns.ac.rs.model.Location;
import uns.ac.rs.model.User;
import uns.ac.rs.repository.LocationRepository;
import uns.ac.rs.repository.UserRepository;

import java.util.List;

@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationRepository locationRepository;

    public List<User> getAllUsers(){
        logger.info("Getting all users");
        return userRepository.listAll();
    }

    public User createUser(UserRequestDTO userRequestDTO) {
        Location location = locationRepository.findByCityAndCountry(userRequestDTO.getLocation().getCity(), userRequestDTO.getLocation().getCountry());
        User user = new User(userRequestDTO, location);
        userRepository.persist(user);
        return user;
    }

    public User updateUser(UserRequestDTO userRequestDTO, String email) {
        User user = userRepository.findByEmail(email);
        setUserAttributes(user, userRequestDTO);
        userRepository.persist(user);
        return user;
    }

    public User retrieveCurrentUser(String email) {
        return userRepository.findByEmail(email);
    }

    private void setUserAttributes(User user, UserRequestDTO userRequestDTO) {
        user.setFirstName(userRequestDTO.getFirstName());
        user.setLastName(userRequestDTO.getLastName());
        user.setUsername(userRequestDTO.getUsername());
        user.setPassword(BcryptUtil.bcryptHash(userRequestDTO.getPassword()));
        user.setLocation(locationRepository.findByCityAndCountry(userRequestDTO.getLocation().getCity(), userRequestDTO.getLocation().getCountry()));
    }
}
