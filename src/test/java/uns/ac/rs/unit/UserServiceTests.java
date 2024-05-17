package uns.ac.rs.unit;

import io.quarkus.elytron.security.common.BcryptUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uns.ac.rs.dto.request.UserRequestDTO;
import uns.ac.rs.model.User;
import uns.ac.rs.repository.UserRepository;
import uns.ac.rs.service.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void testCreateUser() {

        UserRequestDTO mockUserRequestDTO = new UserRequestDTO("some.email@gmail.com", "some-username", "password123", "Someone", "Something", "guest", "Subotica", "Serbia");

        User createdUser = userService.createUser(mockUserRequestDTO);

        verify(userRepository).persist(createdUser);

        assertNotNull(createdUser);
    }

    @Test
    public void testUpdateUser() {

        User mockUser = new User("some.email@gmail.com", "other-username", "other-pw", "guest", "other-name", "other-last-name", "Subotica", "Serbia");
        UserRequestDTO mockUserRequestDTO = new UserRequestDTO("some.email@gmail.com", "some-username", "password123", "Someone", "Something", "guest", "Subotica", "Serbia");

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
