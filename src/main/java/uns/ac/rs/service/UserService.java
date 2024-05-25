package uns.ac.rs.service;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uns.ac.rs.dto.request.UserRequestDTO;
import uns.ac.rs.model.User;
import uns.ac.rs.repository.UserRepository;

import java.util.List;

@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers(){
        logger.info("Getting all users");
        return userRepository.listAll();
    }

    public User createUser(UserRequestDTO userRequestDTO) {
        User user = new User(userRequestDTO);
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

    public User changeAutomaticReservationAcceptanceStatus(String email) {
        User user = userRepository.findByEmail(email);
        user.setAutomaticReservationAcceptance(!user.isAutomaticReservationAcceptance());
        userRepository.persist(user);
        return user;
    }

    public boolean getAutomaticReservationAcceptanceStatus(String email) {
        User user = userRepository.findByEmail(email);
        return user.isAutomaticReservationAcceptance();
    }

    public User appendCancellation(String email) {
        User user = userRepository.findByEmail(email);
        user.setNoCancellations(user.getNoCancellations() + 1);
        userRepository.persist(user);
        return user;
    }

    public int getNoCancellations(String email) {
        User user = userRepository.findByEmail(email);
        return user.getNoCancellations();
    }

    public void deactivateUser(String email) {
        User user = userRepository.findByEmail(email);
        user.setActive(false);
    }

    private void setUserAttributes(User user, UserRequestDTO userRequestDTO) {
        user.setFirstName(userRequestDTO.getFirstName());
        user.setLastName(userRequestDTO.getLastName());
        user.setUsername(userRequestDTO.getUsername());
        user.setPassword(BcryptUtil.bcryptHash(userRequestDTO.getPassword()));
        user.setCity(userRequestDTO.getCity());
        user.setCountry(userRequestDTO.getCountry());
    }
}
