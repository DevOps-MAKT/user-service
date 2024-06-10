package uns.ac.rs.service;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uns.ac.rs.dto.*;
import uns.ac.rs.dto.request.UserRequestDTO;
import uns.ac.rs.model.AccommodationReview;
import uns.ac.rs.model.HostReview;
import uns.ac.rs.model.User;
import uns.ac.rs.repository.AccommodationReviewRepository;
import uns.ac.rs.repository.HostReviewRepository;
import uns.ac.rs.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HostReviewRepository hostReviewRepository;

    @Autowired
    private AccommodationReviewRepository accommodationReviewRepository;

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

    public User updatePassword(String email, PasswordDTO passwordDTO) {
        User user = userRepository.findByEmail(email);
        user.setPassword(BcryptUtil.bcryptHash(passwordDTO.getPassword()));
        userRepository.persist(user);
        return user;
    }

    public User retrieveCurrentUser(String email) {
        return userRepository.findByEmail(email);
    }

    public User changeAutomaticReservationAcceptanceStatus(String email, boolean value) {
        User user = userRepository.findByEmail(email);
        user.setAutomaticReservationAcceptance(value);
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

    public List<HostReviewDTO> retrieveHostReviews(List<String> hostEmails, String guestEmail) {
        List<HostReviewDTO> hostReviews = new ArrayList<>();
        for (String hostEmail: hostEmails) {
            Optional<HostReview> hostReview = hostReviewRepository.findByGuestEmailAndHostEmail(guestEmail, hostEmail);
            if (hostReview.isPresent()) {
                hostReviews.add(new HostReviewDTO(hostReview.get()));
            }
            else {
                hostReviews.add(new HostReviewDTO());
            }
        }
        return hostReviews;
    }

    public HostReview addHostReview(String guestEmail, HostReviewDTO hostReviewDTO) {
        Optional<HostReview> hostReview = hostReviewRepository.findByGuestEmailAndHostEmail(guestEmail, hostReviewDTO.getHostEmail());
        HostReview extractedHostReview;
        if (hostReview.isPresent()) {
            extractedHostReview = hostReview.get();
            extractedHostReview.setRating(hostReviewDTO.getRating());
            extractedHostReview.setTimestamp(hostReviewDTO.getTimestamp());
        }
        else {
            extractedHostReview = new HostReview(hostReviewDTO, guestEmail);
        }
        hostReviewRepository.persist(extractedHostReview);
        return extractedHostReview;
    }

    public HostReview deleteHostReview(String guestEmail, String hostEmail) {
        Optional<HostReview> hostReview = hostReviewRepository.findByGuestEmailAndHostEmail(guestEmail, hostEmail);
        HostReview extractedHostReview = hostReview.get();
        extractedHostReview.setDeleted(true);
        hostReviewRepository.persist(extractedHostReview);
        return extractedHostReview;
    }

    public HostReviewInfoDTO getHostReviewsInfo(String hostEmail) {
        Optional<List<HostReview>> hostReviews = hostReviewRepository.findByHostEmail(hostEmail);
        HostReviewInfoDTO hostReviewInfoDTO = new HostReviewInfoDTO();
        float avgRating = 0;
        List<HostReviewDTO> hostReviewDTOS = new ArrayList<>();
        if (hostReviews.isPresent() && hostReviews.get().size() > 0) {
            List<HostReview> extractedHostReviews = hostReviews.get();
            for (HostReview hostReview: extractedHostReviews) {
                HostReviewDTO hostReviewDTO = new HostReviewDTO(hostReview);
                hostReviewDTOS.add(hostReviewDTO);
                avgRating += hostReview.getRating();
            }
            avgRating /= extractedHostReviews.size();
        }
        hostReviewInfoDTO.setReviews(hostReviewDTOS);
        hostReviewInfoDTO.setAvgRating(avgRating);
        return hostReviewInfoDTO;
    }

    public AccommodationReviewInfoDTO getAccommodationReviewsInfo(Long accommodationId) {
        Optional<List<AccommodationReview>> accommodationReviews = accommodationReviewRepository
                .findByAccommodationId(accommodationId);
        AccommodationReviewInfoDTO accommodationReviewInfoDTO = new AccommodationReviewInfoDTO();
        float avgRating = 0;
        List<AccommodationReviewDTO> accommodationReviewDTOS = new ArrayList<>();
        if (accommodationReviews.isPresent() && accommodationReviews.get().size() > 0) {
            List<AccommodationReview> extractedAccommodationReviews = accommodationReviews.get();
            for (AccommodationReview accommodationReview: extractedAccommodationReviews) {
                AccommodationReviewDTO accommodationReviewDTO = new AccommodationReviewDTO(accommodationReview);
                accommodationReviewDTOS.add(accommodationReviewDTO);
                avgRating += accommodationReview.getRating();
            }
            avgRating /= extractedAccommodationReviews.size();
        }
        accommodationReviewInfoDTO.setReviews(accommodationReviewDTOS);
        accommodationReviewInfoDTO.setAvgRating(avgRating);
        return accommodationReviewInfoDTO;
    }

    public float getAvgRating(Long accommodationId) {
        float avgRating = 0;
        Optional<List<AccommodationReview>> accommodationReviews = accommodationReviewRepository
                .findByAccommodationId(accommodationId);
        if (accommodationReviews.isPresent() && accommodationReviews.get().size() > 0) {
            for (AccommodationReview accommodationReview: accommodationReviews.get()) {
                avgRating += accommodationReview.getRating();
            }
            avgRating /= accommodationReviews.get().size();
        }
        return avgRating;
    }
    public List<AccommodationReviewDTO> retrieveAccommodationReviews(String guestEmail,
                                                                     List<Long> accommodationIds,
                                                                     List<MinAccommodationDTO> minAccommodations) {
        List<AccommodationReviewDTO> accommodationReviews = new ArrayList<>();

        for (Long accommodationId: accommodationIds) {
            Optional<AccommodationReview> accommodationReview = accommodationReviewRepository
                    .findByGuestEmailAndAccommodationId(guestEmail, accommodationId);
            if (accommodationReview.isPresent()) {
                accommodationReviews.add(new AccommodationReviewDTO(accommodationReview.get()));
            }
            else {
                accommodationReviews.add(new AccommodationReviewDTO());
            }
        }
        return accommodationReviews;
    }

    public AccommodationReview addAccommodationReview(String guestEmail, AccommodationReviewDTO accommodationReviewDTO) {
        Optional<AccommodationReview> accommodationReview = accommodationReviewRepository
                .findByGuestEmailAndAccommodationId(guestEmail, accommodationReviewDTO.getAccommodationId());
        AccommodationReview extractedAccommodationReview;
        if (accommodationReview.isPresent()) {
            extractedAccommodationReview = accommodationReview.get();
            extractedAccommodationReview.setRating(accommodationReviewDTO.getRating());
            extractedAccommodationReview.setTimestamp(accommodationReviewDTO.getTimestamp());
        }
        else {
            extractedAccommodationReview = new AccommodationReview(accommodationReviewDTO, guestEmail);
        }
        accommodationReviewRepository.persist(extractedAccommodationReview);
        return extractedAccommodationReview;
    }

    public AccommodationReview deleteAccommodationReview(String guestEmail, Long accommodationId) {
        Optional<AccommodationReview> accommodationReview = accommodationReviewRepository
                .findByGuestEmailAndAccommodationId(guestEmail, accommodationId);
        AccommodationReview extractedAccommodationReview = accommodationReview.get();
        extractedAccommodationReview.setDeleted(true);
        accommodationReviewRepository.persist(extractedAccommodationReview);
        return extractedAccommodationReview;
    }

    public NotificationStatusesDTO retrieveNotificationStatuses(String email) {
        User user = userRepository.findByEmail(email);
        return new NotificationStatusesDTO(user.isReservationRequestedNotificationsActive(),
                user.isReservationCancelledNotificationsActive(),
                user.isHostRatedNotificationsActive(),
                user.isAccommodationRatedNotificationsActive(),
                user.isReservationRequestAnsweredActive());
    }

    public User updateActiveNotificationStatuses(String email, NotificationStatusesDTO notificationStatusesDTO) {
        User user = userRepository.findByEmail(email);
        user.setAccommodationRatedNotificationsActive(notificationStatusesDTO.isAccommodationRatedNotificationsActive());
        user.setHostRatedNotificationsActive(notificationStatusesDTO.isHostRatedNotificationsActive());
        user.setReservationCancelledNotificationsActive(notificationStatusesDTO.isReservationCancelledNotificationsActive());
        user.setReservationRequestedNotificationsActive(notificationStatusesDTO.isReservationRequestedNotificationsActive());
        user.setReservationRequestAnsweredActive(notificationStatusesDTO.isReservationRequestAnsweredActive());
        userRepository.persist(user);
        return user;
    }
    private void setUserAttributes(User user, UserRequestDTO userRequestDTO) {
        user.setFirstName(userRequestDTO.getFirstName());
        user.setLastName(userRequestDTO.getLastName());
        user.setUsername(userRequestDTO.getUsername());
        user.setCity(userRequestDTO.getCity());
        user.setCountry(userRequestDTO.getCountry());
    }

    private List<String> extractNames(List<Long> accommodationIds, List<MinAccommodationDTO> minAccommodations) {
        List<String> names = new ArrayList<>();

        for (Long id : accommodationIds) {
            for (MinAccommodationDTO accommodationDTO : minAccommodations) {
                if (accommodationDTO.getId() == id) {
                    names.add(accommodationDTO.getName());
                    break;
                }
            }
        }

        return names;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public GuestReviewsInfoDTO getUserReviews(String guestEmail) {
        GuestReviewsInfoDTO guestReviews = new GuestReviewsInfoDTO();

        Optional<List<HostReview>> hostReviews = hostReviewRepository.findByGuestEmail(guestEmail);
        Optional<List<AccommodationReview>> accommodationReviews = accommodationReviewRepository
                .findByGuestEmailId(guestEmail);

        List<HostReviewDTO> hostReviewDTOS = new ArrayList<>();
        if (hostReviews.isPresent() && !hostReviews.get().isEmpty()) {
            for (HostReview hostReview: hostReviews.get()) {
                HostReviewDTO hostReviewDTO = new HostReviewDTO(hostReview);
                hostReviewDTOS.add(hostReviewDTO);
            }
        }
        guestReviews.setHostReviews(hostReviewDTOS);

        List<AccommodationReviewDTO> accommodationReviewDTOS = new ArrayList<>();
        if (accommodationReviews.isPresent() && !accommodationReviews.get().isEmpty()) {
            for (AccommodationReview accommodationReview: accommodationReviews.get()) {
                AccommodationReviewDTO accommodationReviewDTO = new AccommodationReviewDTO(accommodationReview);
                accommodationReviewDTOS.add(accommodationReviewDTO);
            }
        }
        guestReviews.setAccommodationReviews(accommodationReviewDTOS);

        return guestReviews;
    }
}
