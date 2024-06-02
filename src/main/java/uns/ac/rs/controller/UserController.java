package uns.ac.rs.controller;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import uns.ac.rs.GeneralResponse;
import uns.ac.rs.MicroserviceCommunicator;
import uns.ac.rs.config.IntegrationConfig;
import uns.ac.rs.dto.*;
import uns.ac.rs.dto.request.UserRequestDTO;
import uns.ac.rs.dto.response.UserResponseDTO;
import uns.ac.rs.model.AccommodationReview;
import uns.ac.rs.model.HostReview;
import uns.ac.rs.model.User;
import uns.ac.rs.service.UserService;

import java.util.List;

@Path("/user")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private MicroserviceCommunicator microserviceCommunicator;

    @Autowired
    private IntegrationConfig config;

    @GET
    @RolesAllowed({"host", "admin", "guest"})
    @Path("/me")
    public String me(@Context SecurityContext securityContext) {
        return securityContext.getUserPrincipal().getName();
    }

    @GET
    @RolesAllowed("admin")
    @Path("/all")
    public List<User> getAllUsers() {
        logger.info("Requested getting all users");
        return userService.getAllUsers();
    }

    @POST
    @Path("/create")
    @PermitAll
    public Response createUser(UserRequestDTO userRequestDTO) {
        try {
            logger.info("Creating a new user with email " + userRequestDTO.getEmail());
            User createdUser = userService.createUser(userRequestDTO);
            logger.info("Successfully created a new user with email " + userRequestDTO.getEmail());
            return Response.status(Response.Status.CREATED)
                    .entity(new GeneralResponse<>(new UserResponseDTO(createdUser), "User successfully registered"))
                    .build();
        } catch (PersistenceException e) {
            logger.warn("Username " + userRequestDTO.getUsername() + " or email " + userRequestDTO.getEmail() + " already exists");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new GeneralResponse<>("", "Username or email already exists"))
                    .build();
        }
    }

    @PUT
    @Path("/update")
    @RolesAllowed({"host", "admin", "guest"})
    public Response updateUser(@Context SecurityContext ctx, UserRequestDTO userRequestDTO) {
        try {
            logger.info("Updating user with email " + userRequestDTO.getEmail());
            User updatedUser = userService.updateUser(userRequestDTO, ctx.getUserPrincipal().getName());
            logger.info("Successfully updated user with email " + userRequestDTO.getEmail());
            return Response.status(Response.Status.OK)
                    .entity(new GeneralResponse<>(new UserResponseDTO(updatedUser), "User successfully updated"))
                    .build();
        } catch (Exception e) {
            logger.warn("An error occurred: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new GeneralResponse<>("", "Something went wrong"))
                    .build();
        }
    }

    @PATCH
    @Path("/update-password")
    @RolesAllowed({"host", "admin", "guest"})
    public Response updatePassword(@Context SecurityContext ctx, PasswordDTO passwordDTO) {
        if (!passwordDTO.getPassword().equals(passwordDTO.getConfirmationPassword())) {
            logger.warn("Provided passwords aren't matching");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new GeneralResponse<>("", "Passwords aren't matching"))
                    .build();
        }
        String email = ctx.getUserPrincipal().getName();
        logger.info("Updating password for user with email " + email);
        User user = userService.updatePassword(email, passwordDTO);
        logger.info("Successfully updated password for user with email " + email);
        return Response
                .ok()
                .entity(new GeneralResponse<>(new UserResponseDTO(user), "Password successfully changed"))
                .build();
    }

    @GET
    @Path("/retrieve-current-user-info")
    @RolesAllowed({"host", "admin", "guest"})
    public Response retrieveCurrentUserInfo(@Context SecurityContext ctx) {
        String email = ctx.getUserPrincipal().getName();
        logger.info("Retrieving info for user with email " + email);
        User user = userService.retrieveCurrentUser(email);
        logger.info("Successfully retrieved info for user with email " + user.getEmail());
        return Response.ok()
                .entity(new GeneralResponse<>(new UserResponseDTO(user), "Info about the logged-in user successfully retrieved"))
                .build();
    }

    @PATCH
    @Path("/change-automatic-reservation-acceptance-status")
    @RolesAllowed("host")
    public Response changeAutomaticReservationAcceptance(@Context SecurityContext ctx) {
        String email = ctx.getUserPrincipal().getName();
        logger.info("Changing automatic reservation status for user with email " + email);
        User user = userService.changeAutomaticReservationAcceptanceStatus(email);
        logger.info("Successfully changed automatic reservation status for user with email " + email);
        return Response
                .ok()
                .entity(new GeneralResponse<>(new UserResponseDTO(user), "Updated automatic reservation acceptance status"))
                .build();
    }


    @GET
    @Path("/get-automatic-reservation-acceptance-status")
    @RolesAllowed("host")
    public Response getAutomaticReservationAcceptanceStatus(@Context SecurityContext ctx) {
        String email = ctx.getUserPrincipal().getName();
        logger.info("Retrieving automatic reservation status for user with email " + email);
        Boolean isAutomaticReservationAcceptanceActive = userService.getAutomaticReservationAcceptanceStatus(email);
        logger.info("Successfully retrieved automatic reservation status for user with email " + email);
        return Response
                .ok()
                .entity(new GeneralResponse<>(isAutomaticReservationAcceptanceActive, "Successfully retrieved automatic reservation acceptance status"))
                .build();
    }

    @GET
    @Path("/append-cancellation")
    @RolesAllowed("guest")
    public Response appendCancellation(@Context SecurityContext ctx) {
        String email = ctx.getUserPrincipal().getName();
        logger.info("Appending the number of reservation cancellations for user with email " + email);
        User user = userService.appendCancellation(email);
        logger.info("Successfully appended the number of reservation cancellations for user with email " + email);
        return Response
                .ok()
                .entity(new GeneralResponse<>(user.getNoCancellations(), "Successfully appended number of cancellations"))
                .build();
    }

    @GET
    @Path("/no-cancellations/{guest_email}")
    @RolesAllowed("host")
    public Response getNoCancellations(@Context SecurityContext ctx, @PathParam("guest_email") String guestEmail) {
        logger.info("Retrieving the number of reservation cancellations for user with email " + guestEmail);
        int noCancellations = userService.getNoCancellations(guestEmail);
        logger.info("Successfully retrieved the number of reservation cancellations for user with email " + guestEmail);
        return Response
                .ok()
                .entity(new GeneralResponse<>(noCancellations, "Successfully retrieved number of cancellations"))
                .build();
    }

    @PATCH
    @Path("/terminate-guest")
    @RolesAllowed("guest")
    public Response terminateGuestAccount(@Context SecurityContext ctx, @HeaderParam("Authorization") String authorizationHeader) {
        String email = ctx.getUserPrincipal().getName();
        logger.info("Retrieving whether reservations are active for the user with email " + email);
        GeneralResponse response = microserviceCommunicator.processResponse(
                config.reservationServiceAPI() + "/reservation/are-reservations-active/" + email,
                "GET",
                authorizationHeader);
        if ((boolean) response.getData()) {
            logger.info("Reservations are active for user with email " + email);
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(new GeneralResponse<>(false, "There are active reservations"))
                    .build();
        }
        logger.info("Reservations are not active for user with email " + email);
        logger.info("Deactivating user with email " + email);
        userService.deactivateUser(email);
        logger.info("Successfully deactivated user with email " + email);
        return Response
                .status(Response.Status.OK)
                .entity(new GeneralResponse<>(true, "Successfully terminated account"))
                .build();
    }

    @PATCH
    @Path("/terminate-host")
    @RolesAllowed("host")
    public Response terminateHostAccount(@Context SecurityContext ctx, @HeaderParam("Authorization") String authorizationHeader) {
        String email = ctx.getUserPrincipal().getName();
        logger.info("Checking whether active reservations exist for user with email " + email);
        GeneralResponse response = microserviceCommunicator.processResponse(
                config.reservationServiceAPI() + "/reservation/do-active-reservations-exist/" + email,
                "GET",
                authorizationHeader);
        if ((boolean) response.getData()) {
            logger.info("Active reservations exist for user with email " + email);
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(new GeneralResponse<>(false, "There are active reservations"))
                    .build();
        }
        logger.info("Active reservations do not exist for user with email " + email);
        logger.info("Deactivating user with email " + email);
        userService.deactivateUser(email);
        logger.info("Successfully deactivated user with email " + email);
        logger.info("Deactivating accommodations from user with email " + email);
        GeneralResponse successfulAccommodationDeletion = microserviceCommunicator.processResponse(
                config.accommodationServiceAPI() + "/accommodation/deactivate-hosts-accommodations/" + email,
                "DELETE",
                authorizationHeader);
        if (!(boolean) successfulAccommodationDeletion.getData())  {
            logger.warn("Something went wrong while deactivating accommodations from user with email " + email);
            return Response
                    .status(Response.Status.OK)
                    .entity(new GeneralResponse<>(false, "Something went wrong while terminating accommodations"))
                    .build();
        }
        logger.info("Successfully deactivated accommodations from the user with email " + email);
        return Response
                .status(Response.Status.OK)
                .entity(new GeneralResponse<>(true, "Successfully terminated account"))
                .build();
    }

    @GET
    @Path("/host-reviews")
    @RolesAllowed("guest")
    public Response getHostReviews(@Context SecurityContext ctx) {
        String email = ctx.getUserPrincipal().getName();
        logger.info("Retrieving host emails where the user with email " + email + " has had reservations");
        GeneralResponse response = microserviceCommunicator.processResponse(
                config.reservationServiceAPI() + "/retrieve-reservation-hosts/" + email,
                "GET",
                "");
        logger.info("Successfully retrieved host emails where the user with email " + email + " has had reservations");
        List<String> hostEmails = (List<String>) response.getData();
        logger.info("Retrieving reviews for the hosts from the user with email " + email);
        List<HostReviewDTO> reviewDTOS = userService.retrieveHostReviews(hostEmails, email);
        logger.info("Successfully retrieved reviews for the hosts from the user with email " + email);
        return Response
                .ok()
                .entity(new GeneralResponse<>(reviewDTOS, "Successfully retrieved reviews"))
                .build();
    }

    @GET
    @Path("/accommodation-reviews")
    @RolesAllowed("guest")
    public Response getAccommodationReviews(@Context SecurityContext ctx) {
        String email = ctx.getUserPrincipal().getName();
        logger.info("Retrieving accommodations where the user with email " + email + " has had reservations");
        GeneralResponse response = microserviceCommunicator.processResponse(
                config.reservationServiceAPI() + "/retrieve-reservation-accommodations/" + email,
                "GET",
                "");
        List<Long> accommodationIds = (List<Long>) response.getData();
        logger.info("Successfully retrieved accommodations where the user with email " + email + " has had reservations");

        logger.info("Retrieving info about accommodations where the user with email " + email + " has had reservations");
        GeneralResponse minAccommodations = microserviceCommunicator.processResponse(
                config.accommodationServiceAPI() + "/retrieve-min-accommodations",
                "GET",
                "");
        List<MinAccommodationDTO> minAccommodationDTOS = (List<MinAccommodationDTO>) minAccommodations.getData();
        logger.info("Successfully retrieved accommodation info where the user with email " + email + " has had reservations");
        logger.info("Retrieving accommodation reviews where the user with email " + email + " has had reservations");
        List<AccommodationReviewDTO> accommodationReviews = userService.retrieveAccommodationReviews(email, accommodationIds, minAccommodationDTOS);
        logger.info("Successfully retrieved accommodation reviews where the user with email " + email + " has had reservations");
        return Response
                .ok()
                .entity(new GeneralResponse<>(accommodationReviews, "Successfully retrieved reviews"))
                .build();
    }

    @PUT
    @Path("/add-host-review")
    @RolesAllowed("guest")
    public Response addHostReview(@Context SecurityContext ctx, HostReviewDTO hostReviewDTO) {
        String email = ctx.getUserPrincipal().getName();
        logger.info("Adding host review where the user with email " + email + " has had reservations");
        HostReview addedReview = userService.addHostReview(email, hostReviewDTO);
        logger.info("Successfully added a host review where the user with email " + email + " has had reservations");
        return Response
                .ok()
                .entity(new GeneralResponse<>(new HostReviewDTO(addedReview), "Successfully added/updated host review"))
                .build();
    }

    @PUT
    @Path("/add-accommodation-review")
    @RolesAllowed("guest")
    public Response addAccommodationReview(@Context SecurityContext ctx, AccommodationReviewDTO accommodationReviewDTO) {
        String email = ctx.getUserPrincipal().getName();
        logger.info("Adding accommodation review where the user with email " + email + " has had reservations");
        AccommodationReview accommodationReview = userService.addAccommodationReview(email, accommodationReviewDTO);
        logger.info("Successfully added an accommodation review where the user with email " + email + " has had reservations");
        return Response
                .ok()
                .entity(new GeneralResponse<>(new AccommodationReviewDTO(accommodationReview), "Successfully added/updated accommodation review"))
                .build();
    }
    @DELETE
    @Path("/delete-host-review/{host_email}")
    @RolesAllowed("guest")
    public Response deleteHostReview(@Context SecurityContext ctx, @PathParam("host_email") String hostEmail) {
        String email = ctx.getUserPrincipal().getName();
        logger.info("Deleting a host review where the user with email " + email + " has had reservations");
        HostReview deletedReview = userService.deleteHostReview(email, hostEmail);
        logger.info("Successfully deleted the host review where the user with email " + email + " has had reservations");
        return Response
                .ok()
                .entity(new GeneralResponse<>(new HostReviewDTO(deletedReview), "Successfully deleted host review"))
                .build();
    }

    @DELETE
    @Path("/delete-accommodation-review/{accommodation_name}")
    @RolesAllowed("guest")
    public Response deleteAccommodationReview(@Context SecurityContext ctx, @PathParam("accommodation_name") String accommodationName) {
        String email = ctx.getUserPrincipal().getName();
        logger.info("Deleting an accommodation review where the user with email " + email + " has had reservations");
        AccommodationReview deletedReview = userService.deleteAccommodationReview(email, accommodationName);
        logger.info("Successfully deleted the accommodation review where the user with email " + email + " has had reservations");
        return Response
                .ok()
                .entity(new GeneralResponse<>(new AccommodationReviewDTO(deletedReview), "Successfully deleted accommodation review"))
                .build();
    }

    @GET
    @Path("/host-reviews-info")
    @RolesAllowed("host")
    public Response getHostReviewsInfo(@Context SecurityContext ctx) {
        String email = ctx.getUserPrincipal().getName();
        logger.info("Retrieving reviews and average rating of a host with email " + email);
        HostReviewInfoDTO hostReviewInfoDTO = userService.getHostReviewsInfo(email);
        logger.info("Successfully retrieved reviews and average rating of a host with email " + email);
        return Response
                .ok()
                .entity(new GeneralResponse<>(hostReviewInfoDTO, "Successfully retrieved host reviews info"))
                .build();
    }

    @GET
    @Path("/accommodation-reviews-info/{accommodation_name}")
    @PermitAll
    public Response getAccommodationReviewsInfo(@PathParam("accommodation_name") String accommodationName) {
        logger.info("Retrieving reviews and average rating for accommodation with name " + accommodationName);
        AccommodationReviewInfoDTO accommodationReviewInfoDTO = userService.getAccommodationReviewsInfo(accommodationName);
        logger.info("Successsfully retrieved reviews and average rating for accommodation with name " + accommodationName);
        return Response
                .ok()
                .entity(new GeneralResponse<>(accommodationReviewInfoDTO, "Successfully retrieved accommodation reviews info"))
                .build();
    }

    @GET
    @Path("/avg-rating/{accommodation_name}")
    @PermitAll
    public Response getAvgRating(@PathParam("accommodation_name") String accommodationName) {
        logger.info("Retrieving average rating for accommodation with name " + accommodationName);
        float avgRating = userService.getAvgRating(accommodationName);
        logger.info("Successfully retrieved average rating for accommodation with name " + accommodationName);
        return Response
                .ok()
                .entity(new GeneralResponse<>(avgRating, "Successfully retrieved accommodation rating"))
                .build();
    }

}
