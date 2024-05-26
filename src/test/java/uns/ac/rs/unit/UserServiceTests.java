package uns.ac.rs.unit;

import io.quarkus.elytron.security.common.BcryptUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uns.ac.rs.dto.HostReviewDTO;
import uns.ac.rs.dto.HostReviewInfoDTO;
import uns.ac.rs.dto.request.UserRequestDTO;
import uns.ac.rs.model.HostReview;
import uns.ac.rs.model.User;
import uns.ac.rs.repository.HostReviewRepository;
import uns.ac.rs.repository.UserRepository;
import uns.ac.rs.service.UserService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private HostReviewRepository hostReviewRepository;

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

    @Test
    public void testChangeAutomaticReservationAcceptanceStatus() {
        User mockUser = new User("some.email@gmail.com", "other-username", "other-pw", "host", "other-name", "other-last-name", "Subotica", "Serbia");
        when(userRepository.findByEmail("some.email@gmail.com")).thenReturn(mockUser);
        User user = userService.changeAutomaticReservationAcceptanceStatus("some.email@gmail.com");

        verify(userRepository).persist(user);

        assertNotNull(user);
        assertTrue(user.isAutomaticReservationAcceptance());
    }

    @Test
    public void testGetAutomaticReservationAcceptanceStatus() {
        User mockUser = new User("some.email@gmail.com", "other-username", "other-pw", "host", "other-name", "other-last-name", "Subotica", "Serbia");
        when(userRepository.findByEmail("some.email@gmail.com")).thenReturn(mockUser);
        boolean status = userService.getAutomaticReservationAcceptanceStatus("some.email@gmail.com");

        assertFalse(status);
    }

    @Test
    public void testAppendCancellation() {
        User mockUser = new User("some.email@gmail.com", "other-username", "other-pw", "host", "other-name", "other-last-name", "Subotica", "Serbia");
        when(userRepository.findByEmail("some.email@gmail.com")).thenReturn(mockUser);
        User user = userService.appendCancellation("some.email@gmail.com");

        assertEquals(user.getNoCancellations(), 1);
    }

    @Test
    public void testGetNOCancellations() {
        User mockUser = new User("some.email@gmail.com", "other-username", "other-pw", "host", "other-name", "other-last-name", "Subotica", "Serbia");
        when(userRepository.findByEmail("some.email@gmail.com")).thenReturn(mockUser);
        int noCancellations = userService.getNoCancellations("some.email@gmail.com");

        assertEquals(noCancellations, 0);
    }

    @Test
    public void testRetrieveHostReviews() {
        List<String> hostEmails = Arrays.asList("host1@example.com", "host2@example.com");
        String guestEmail = "guest@example.com";
        HostReview hostReview1 = new HostReview();
        hostReview1.setId(1L);
        hostReview1.setHostEmail("host1@example.com");
        hostReview1.setGuestEmail("guest@example.com");
        hostReview1.setRating(5);
        HostReview hostReview2 = new HostReview();
        hostReview2.setId(2L);
        hostReview2.setHostEmail("host2@example.com");
        hostReview2.setGuestEmail("guest@example.com");
        hostReview2.setRating(4);
        when(hostReviewRepository.findByGuestEmailAndHostEmail(eq(guestEmail), anyString()))
                .thenReturn(java.util.Optional.ofNullable(hostReview1))
                .thenReturn(java.util.Optional.ofNullable(hostReview2));

        List<HostReviewDTO> hostReviews = userService.retrieveHostReviews(hostEmails, guestEmail);

        assertEquals(2, hostReviews.size());
        assertEquals("host1@example.com", hostReviews.get(0).getHostEmail());
        assertEquals(5, hostReviews.get(0).getRating());
        assertEquals("host2@example.com", hostReviews.get(1).getHostEmail());
        assertEquals(4, hostReviews.get(1).getRating());
    }

    @Test
    public void testAddHostReview() {
        String guestEmail = "guest@example.com";
        HostReviewDTO hostReviewDTO = new HostReviewDTO();
        hostReviewDTO.setHostEmail("host@example.com");
        hostReviewDTO.setRating(5);
        hostReviewDTO.setTimestamp(System.currentTimeMillis());
        HostReview existingHostReview = new HostReview();
        existingHostReview.setId(1L);
        existingHostReview.setHostEmail("host@example.com");
        existingHostReview.setGuestEmail("guest@example.com");
        existingHostReview.setRating(3);
        existingHostReview.setTimestamp(System.currentTimeMillis() - 3600000); // One hour ago
        when(hostReviewRepository.findByGuestEmailAndHostEmail(eq(guestEmail), eq(hostReviewDTO.getHostEmail()))).thenReturn(java.util.Optional.ofNullable(existingHostReview));

        HostReview addedHostReview = userService.addHostReview(guestEmail, hostReviewDTO);

        assertNotNull(addedHostReview);
        assertEquals(hostReviewDTO.getHostEmail(), addedHostReview.getHostEmail());
        assertEquals(hostReviewDTO.getRating(), addedHostReview.getRating());
        assertEquals(hostReviewDTO.getTimestamp(), addedHostReview.getTimestamp());
    }

    @Test
    public void testAddHostReviewNew() {
        String guestEmail = "guest@example.com";
        HostReviewDTO hostReviewDTO = new HostReviewDTO();
        hostReviewDTO.setHostEmail("host@example.com");
        hostReviewDTO.setRating(5);
        hostReviewDTO.setTimestamp(System.currentTimeMillis());
        when(hostReviewRepository.findByGuestEmailAndHostEmail(eq(guestEmail), eq(hostReviewDTO.getHostEmail()))).thenReturn(java.util.Optional.empty());

        HostReview addedHostReview = userService.addHostReview(guestEmail, hostReviewDTO);

        assertNotNull(addedHostReview);
        assertEquals(hostReviewDTO.getHostEmail(), addedHostReview.getHostEmail());
        assertEquals(hostReviewDTO.getRating(), addedHostReview.getRating());
        assertEquals(hostReviewDTO.getTimestamp(), addedHostReview.getTimestamp());
    }

    @Test
    public void testDeleteHostReview() {
        String guestEmail = "guest@example.com";
        String hostEmail = "host@example.com";
        HostReview hostReview = new HostReview();
        hostReview.setId(1L);
        hostReview.setGuestEmail(guestEmail);
        hostReview.setHostEmail(hostEmail);
        hostReview.setDeleted(false);
        when(hostReviewRepository.findByGuestEmailAndHostEmail(guestEmail, hostEmail)).thenReturn(java.util.Optional.ofNullable(hostReview));

        HostReview deletedHostReview = userService.deleteHostReview(guestEmail, hostEmail);

        assertNotNull(deletedHostReview);
        assertTrue(deletedHostReview.isDeleted());
    }

    @Test
    public void testGetHostReviewsInfo() {
        String hostEmail = "host@example.com";
        HostReview hostReview1 = new HostReview();
        hostReview1.setId(1L);
        hostReview1.setHostEmail(hostEmail);
        hostReview1.setRating(5);
        HostReview hostReview2 = new HostReview();
        hostReview2.setId(2L);
        hostReview2.setHostEmail(hostEmail);
        hostReview2.setRating(4);
        List<HostReview> hostReviewsList = Arrays.asList(hostReview1, hostReview2);
        when(hostReviewRepository.findByHostEmail(hostEmail)).thenReturn(Optional.of(hostReviewsList));

        HostReviewInfoDTO hostReviewInfoDTO = userService.getHostReviewsInfo(hostEmail);

        assertNotNull(hostReviewInfoDTO);
        assertEquals(2, hostReviewInfoDTO.getReviews().size());
        assertEquals(4.5f, hostReviewInfoDTO.getAvgRating());
    }

    @Test
    public void testGetHostReviewsInfoNoReviews() {
        String hostEmail = "host@example.com";
        when(hostReviewRepository.findByHostEmail(hostEmail)).thenReturn(Optional.empty());

        HostReviewInfoDTO hostReviewInfoDTO = userService.getHostReviewsInfo(hostEmail);

        assertNotNull(hostReviewInfoDTO);
        assertEquals(0, hostReviewInfoDTO.getReviews().size());
        assertEquals(0, hostReviewInfoDTO.getAvgRating());
    }
}
