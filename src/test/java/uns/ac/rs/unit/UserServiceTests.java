package uns.ac.rs.unit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uns.ac.rs.dto.*;
import uns.ac.rs.dto.request.UserRequestDTO;
import uns.ac.rs.model.AccommodationReview;
import uns.ac.rs.model.HostReview;
import uns.ac.rs.model.User;
import uns.ac.rs.repository.AccommodationReviewRepository;
import uns.ac.rs.repository.HostReviewRepository;
import uns.ac.rs.repository.UserRepository;
import uns.ac.rs.service.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private HostReviewRepository hostReviewRepository;

    @Mock
    private AccommodationReviewRepository accommodationReviewRepository;
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
        assertEquals(updatedUser.getFirstName(), mockUserRequestDTO.getFirstName());
        assertEquals(updatedUser.getLastName(), mockUserRequestDTO.getLastName());
    }

    @Test
    public void testChangeAutomaticReservationAcceptanceStatus() {
        User mockUser = new User("some.email@gmail.com", "other-username", "other-pw", "host", "other-name", "other-last-name", "Subotica", "Serbia");
        when(userRepository.findByEmail("some.email@gmail.com")).thenReturn(mockUser);
        User user = userService.changeAutomaticReservationAcceptanceStatus("some.email@gmail.com", true);

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

    @Test
    public void testGetAccommodationReviewsInfo() {
        // Mock data
        Long accommodationId = 1L;
        AccommodationReview accommodationReview1 = new AccommodationReview();
        accommodationReview1.setId(1L);
        accommodationReview1.setAccommodationId(accommodationId);
        accommodationReview1.setRating(5);
        AccommodationReview accommodationReview2 = new AccommodationReview();
        accommodationReview2.setId(2L);
        accommodationReview2.setAccommodationId(accommodationId);
        accommodationReview2.setRating(4);
        when(accommodationReviewRepository.findByAccommodationId(accommodationId))
                .thenReturn(Optional.of(Arrays.asList(accommodationReview1, accommodationReview2)));

        AccommodationReviewInfoDTO reviewInfoDTO = userService.getAccommodationReviewsInfo(accommodationId);

        assertNotNull(reviewInfoDTO);
        assertEquals(2, reviewInfoDTO.getReviews().size());
        assertEquals(4.5f, reviewInfoDTO.getAvgRating());
    }

    @Test
    public void testRetrieveAccommodationReviews() {
        String guestEmail = "guest@example.com";
        List<Long> accommodationIds = Arrays.asList(1L, 2L);
        List<MinAccommodationDTO> minAccommodations = Arrays.asList(
                new MinAccommodationDTO(1L, "Accommodation 1"),
                new MinAccommodationDTO(2L, "Accommodation 2")
        );

        AccommodationReview accommodationReview1 = new AccommodationReview();
        accommodationReview1.setId(1L);
        accommodationReview1.setAccommodationId(1L);
        accommodationReview1.setRating(5);
        AccommodationReview accommodationReview2 = new AccommodationReview();
        accommodationReview2.setId(2L);
        accommodationReview2.setAccommodationId(2L);
        accommodationReview2.setRating(4);
        when(accommodationReviewRepository.findByGuestEmailAndAccommodationId(eq(guestEmail), anyLong()))
                .thenReturn(Optional.of(accommodationReview1), Optional.of(accommodationReview2));

        List<AccommodationReviewDTO> reviewDTOs = userService.retrieveAccommodationReviews(guestEmail, accommodationIds, minAccommodations);

        assertNotNull(reviewDTOs);
        assertEquals(2, reviewDTOs.size());
        assertEquals(5, reviewDTOs.get(0).getRating());
        assertEquals(4, reviewDTOs.get(1).getRating());
    }

    @Test
    public void testAddAccommodationReview() {
        String guestEmail = "guest@example.com";
        AccommodationReviewDTO reviewDTO = new AccommodationReviewDTO();
        reviewDTO.setAccommodationId(1L);
        reviewDTO.setRating(5);
        reviewDTO.setTimestamp(System.currentTimeMillis());

        AccommodationReview existingReview = new AccommodationReview();
        existingReview.setId(1L);
        existingReview.setGuestEmail(guestEmail);
        existingReview.setAccommodationId(1L);
        existingReview.setRating(4);
        existingReview.setTimestamp(System.currentTimeMillis() - 1000);
        when(accommodationReviewRepository.findByGuestEmailAndAccommodationId(guestEmail, reviewDTO.getAccommodationId()))
                .thenReturn(Optional.of(existingReview));

        AccommodationReview addedReview = userService.addAccommodationReview(guestEmail, reviewDTO);

        assertNotNull(addedReview);
        assertEquals(existingReview.getId(), addedReview.getId());
        assertEquals(5, addedReview.getRating());
    }

    @Test
    public void testAddNewAccommodationReview() {
        String guestEmail = "guest@example.com";
        AccommodationReviewDTO reviewDTO = new AccommodationReviewDTO();
        reviewDTO.setAccommodationId(1L);
        reviewDTO.setRating(5);
        reviewDTO.setTimestamp(System.currentTimeMillis());

        when(accommodationReviewRepository.findByGuestEmailAndAccommodationId(guestEmail, reviewDTO.getAccommodationId()))
                .thenReturn(Optional.empty());

        AccommodationReview addedReview = userService.addAccommodationReview(guestEmail, reviewDTO);

        assertNotNull(addedReview);
        assertEquals(guestEmail, addedReview.getGuestEmail());
        assertEquals(1L, addedReview.getAccommodationId());
        assertEquals(5, addedReview.getRating());
    }
    @Test
    public void testDeleteAccommodationReview() {
        String guestEmail = "guest@example.com";
        Long accommodationId = 1L;
        AccommodationReview existingReview = new AccommodationReview();
        existingReview.setId(1L);
        existingReview.setGuestEmail(guestEmail);
        existingReview.setAccommodationId(accommodationId);
        existingReview.setDeleted(false);
        when(accommodationReviewRepository.findByGuestEmailAndAccommodationId(guestEmail, accommodationId))
                .thenReturn(Optional.of(existingReview));

        AccommodationReview deletedReview = userService.deleteAccommodationReview(guestEmail, accommodationId);

        assertNotNull(deletedReview);
        assertTrue(deletedReview.isDeleted());
        verify(accommodationReviewRepository, times(1)).persist(existingReview);
    }
    @Test
    public void testGetAvgRating() {

        // Create test data
        List<AccommodationReview> reviews = new ArrayList<>();
        reviews.add(new AccommodationReview("email1","hemail1", 1L, 123456L, 4, false));
        reviews.add(new AccommodationReview("email2", "hemail2", 1L, 123457L, 5, false));
        reviews.add(new AccommodationReview("email3", "hemail3", 1L, 123458L, 3, false));

        when(accommodationReviewRepository.findByAccommodationId(1L))
                .thenReturn(Optional.of(reviews));

        float avgRating = userService.getAvgRating(1L);

        assertEquals(4.0f, avgRating, 0.01);
    }
    @Test
    public void testRetrieveNotificationStatuses() {
        User user = new User();
        user.setReservationRequestAnsweredActive(true);
        user.setReservationCancelledNotificationsActive(true);
        user.setReservationRequestedNotificationsActive(true);
        user.setHostRatedNotificationsActive(false);
        user.setAccommodationRatedNotificationsActive(false);

        when(userRepository.findByEmail("email")).thenReturn(user);

        NotificationStatusesDTO notificationStatusesDTO = userService.retrieveNotificationStatuses("email");

        assertTrue(notificationStatusesDTO.isReservationRequestedNotificationsActive());
        assertTrue(notificationStatusesDTO.isReservationCancelledNotificationsActive());
        assertTrue(notificationStatusesDTO.isReservationRequestedNotificationsActive());
        assertFalse(notificationStatusesDTO.isHostRatedNotificationsActive());
        assertFalse(notificationStatusesDTO.isAccommodationRatedNotificationsActive());
    }

    @Test
    public void testUpdateNotificationStatuses() {
        User user = new User();
        user.setReservationRequestAnsweredActive(true);
        user.setReservationCancelledNotificationsActive(true);
        user.setReservationRequestedNotificationsActive(true);
        user.setHostRatedNotificationsActive(false);
        user.setAccommodationRatedNotificationsActive(false);

        NotificationStatusesDTO notificationStatusesDTO1 = new NotificationStatusesDTO();
        notificationStatusesDTO1.setReservationCancelledNotificationsActive(false);
        notificationStatusesDTO1.setHostRatedNotificationsActive(false);
        notificationStatusesDTO1.setReservationRequestedNotificationsActive(false);
        notificationStatusesDTO1.setAccommodationRatedNotificationsActive(false);
        notificationStatusesDTO1.setReservationRequestAnsweredActive(false);

        when(userRepository.findByEmail("email")).thenReturn(user);

        User updatedUser = userService.updateActiveNotificationStatuses("email", notificationStatusesDTO1);

        assertFalse(updatedUser.isReservationRequestedNotificationsActive());
        assertFalse(updatedUser.isReservationCancelledNotificationsActive());
        assertFalse(updatedUser.isReservationRequestedNotificationsActive());
        assertFalse(updatedUser.isHostRatedNotificationsActive());
        assertFalse(updatedUser.isAccommodationRatedNotificationsActive());
    }


}
