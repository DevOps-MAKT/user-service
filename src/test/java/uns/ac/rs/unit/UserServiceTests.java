package uns.ac.rs.unit;

import io.quarkus.elytron.security.common.BcryptUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.wildfly.security.password.interfaces.BCryptPassword;
import uns.ac.rs.dto.LocationDTO;
import uns.ac.rs.dto.request.UserRequestDTO;
import uns.ac.rs.model.Location;
import uns.ac.rs.model.User;
import uns.ac.rs.repository.LocationRepository;
import uns.ac.rs.repository.UserRepository;
import uns.ac.rs.service.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
    @Mock
    private LocationRepository locationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void testCreateUser() {

        Location mockLocation = new Location("Subotica", "Serbia");
        LocationDTO mockLocationDTO = new LocationDTO(mockLocation);
        UserRequestDTO mockUserRequestDTO = new UserRequestDTO("some.email@gmail.com", "some-username", "password123", "Someone", "Something", "guest", mockLocationDTO);

        when(locationRepository.findByCityAndCountry("Subotica", "Serbia")).thenReturn(mockLocation);

        User createdUser = userService.createUser(mockUserRequestDTO);

        verify(userRepository).persist(createdUser);

        assertNotNull(createdUser);
    }

    @Test
    public void testUpdateUser() {

        Location mockLocation = new Location("Subotica", "Serbia");
        LocationDTO mockLocationDTO = new LocationDTO(mockLocation);
        User mockUser = new User("some.email@gmail.com", "other-username", "other-pw", "guest", "other-name", "other-last-name", mockLocation);
        UserRequestDTO mockUserRequestDTO = new UserRequestDTO("some.email@gmail.com", "some-username", "password123", "Someone", "Something", "guest", mockLocationDTO);

        when(locationRepository.findByCityAndCountry("Subotica", "Serbia")).thenReturn(mockLocation);
        when(userRepository.findByEmail("some.email@gmail.com")).thenReturn(mockUser);
        User updatedUser = userService.updateUser(mockUserRequestDTO, "some.email@gmail.com");

        verify(userRepository).persist(updatedUser);

        assertNotNull(updatedUser);
        assertEquals(updatedUser.getUsername(), mockUserRequestDTO.getUsername());
        assertTrue(BcryptUtil.matches(mockUserRequestDTO.getPassword(), updatedUser.getPassword()));
        assertEquals(updatedUser.getFirstName(), mockUserRequestDTO.getFirstName());
        assertEquals(updatedUser.getLastName(), mockUserRequestDTO.getLastName());
    }
}
