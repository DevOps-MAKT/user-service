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
        if (hostReviews.isPresent()) {
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

    public AccommodationReviewInfoDTO getAccommodationReviewsInfo(String accommodationName) {
        Optional<List<AccommodationReview>> accommodationReviews = accommodationReviewRepository
                .findByAccommodationName(accommodationName);
        AccommodationReviewInfoDTO accommodationReviewInfoDTO = new AccommodationReviewInfoDTO();
        float avgRating = 0;
        List<AccommodationReviewDTO> accommodationReviewDTOS = new ArrayList<>();
        if (accommodationReviews.isPresent()) {
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
    public List<AccommodationReviewDTO> retrieveAccommodationReviews(String guestEmail,
                                                                     List<Long> accommodationIds,
                                                                     List<MinAccommodationDTO> minAccommodations) {
        List<AccommodationReviewDTO> accommodationReviews = new ArrayList<>();
        List<String> accommodationNames = extractNames(accommodationIds, minAccommodations);

        for (String accommodationName: accommodationNames) {
            Optional<AccommodationReview> accommodationReview = accommodationReviewRepository
                    .findByGuestEmailAndAccommodationName(guestEmail, accommodationName);
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
                .findByGuestEmailAndAccommodationName(guestEmail, accommodationReviewDTO.getAccommodationName());
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

    public AccommodationReview deleteAccommodationReview(String guestEmail, String accommodationName) {
        Optional<AccommodationReview> accommodationReview = accommodationReviewRepository
                .findByGuestEmailAndAccommodationName(guestEmail, accommodationName);
        AccommodationReview extractedAccommodationReview = accommodationReview.get();
        extractedAccommodationReview.setDeleted(true);
        accommodationReviewRepository.persist(extractedAccommodationReview);
        return extractedAccommodationReview;
    }
    private void setUserAttributes(User user, UserRequestDTO userRequestDTO) {
        user.setFirstName(userRequestDTO.getFirstName());
        user.setLastName(userRequestDTO.getLastName());
        user.setUsername(userRequestDTO.getUsername());
        user.setPassword(BcryptUtil.bcryptHash(userRequestDTO.getPassword()));
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
}
