package uns.ac.rs.unit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uns.ac.rs.dto.LocationDTO;
import uns.ac.rs.dto.request.UserRequestDTO;
import uns.ac.rs.model.Location;
import uns.ac.rs.model.User;
import uns.ac.rs.repository.LocationRepository;
import uns.ac.rs.repository.UserRepository;
import uns.ac.rs.service.UserService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
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
}
