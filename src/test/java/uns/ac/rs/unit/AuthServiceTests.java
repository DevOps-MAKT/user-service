package uns.ac.rs.unit;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import io.quarkus.elytron.security.common.BcryptUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import uns.ac.rs.dto.LoginDTO;
import uns.ac.rs.model.Location;
import uns.ac.rs.model.User;
import uns.ac.rs.repository.UserRepository;
import uns.ac.rs.service.AuthService;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTests {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    public void testLogin_UserFound_PasswordMatch() {
        LoginDTO mockLoginDTO = new LoginDTO("some-username", "admin123");

        User mockUser = new User("some.email@gmail.com", "some-username", "$2a$12$0Z2p.u8SdUm71I7uzaEgDOnay5dDtbiV2S3QdXfKccBlzPO0JRkA.", "Someone", "Something", "guest", new Location("Subotica", "Serbia"));

        when(userRepository.findByUsername("some-username")).thenReturn(mockUser);

        Optional<User> loggedInUser = authService.login(mockLoginDTO);

        verify(userRepository).findByUsername("some-username");

        assertTrue(loggedInUser.isPresent());
        assertEquals(mockUser, loggedInUser.get());
    }

    @Test
    public void testLogin_UserNotFound() {
        LoginDTO mockLoginDTO = new LoginDTO("nonexistent-username", "password123");

        when(userRepository.findByUsername("nonexistent-username")).thenReturn(null);

        Optional<User> loggedInUser = authService.login(mockLoginDTO);

        verify(userRepository).findByUsername("nonexistent-username");

        assertFalse(loggedInUser.isPresent());
    }

    @Test
    public void testLogin_PasswordDoesNotMatch() {
        LoginDTO mockLoginDTO = new LoginDTO("some-username", "incorrect_password");

        User mockUser = new User("some.email@gmail.com", "some-username", "$2a$12$0Z2p.u8SdUm71I7uzaEgDOnay5dDtbiV2S3QdXfKccBlzPO0JRkA.", "Someone", "Something", "guest", new Location("Subotica", "Serbia"));

        when(userRepository.findByUsername("some-username")).thenReturn(mockUser);

        Optional<User> loggedInUser = authService.login(mockLoginDTO);

        verify(userRepository).findByUsername("some-username");

        assertFalse(loggedInUser.isPresent());
    }
}
