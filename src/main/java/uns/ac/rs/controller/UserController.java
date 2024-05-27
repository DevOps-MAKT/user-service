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
            User createdUser = userService.createUser(userRequestDTO);
            return Response.status(Response.Status.CREATED)
                    .entity(new GeneralResponse<>(new UserResponseDTO(createdUser), "User successfully registered"))
                    .build();
        } catch (PersistenceException e) {
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
            User updatedUser = userService.updateUser(userRequestDTO, ctx.getUserPrincipal().getName());
            return Response.status(Response.Status.OK)
                    .entity(new GeneralResponse<>(new UserResponseDTO(updatedUser), "User successfully updated"))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new GeneralResponse<>("", "Something went wrong"))
                    .build();
        }
    }

    @GET
    @Path("/retrieve-current-user-info")
    @RolesAllowed({"host", "admin", "guest"})
    public Response retrieveCurrentUserInfo(@Context SecurityContext ctx) {
        String email = ctx.getUserPrincipal().getName();
        User user = userService.retrieveCurrentUser(email);
        return Response.ok()
                .entity(new GeneralResponse<>(new UserResponseDTO(user), "Info about the logged-in user successfully retrieved"))
                .build();
    }

    @PATCH
    @Path("/change-automatic-reservation-acceptance-status")
    @RolesAllowed("host")
    public Response changeAutomaticReservationAcceptance(@Context SecurityContext ctx) {
        String email = ctx.getUserPrincipal().getName();
        User user = userService.changeAutomaticReservationAcceptanceStatus(email);
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
        Boolean isAutomaticReservationAcceptanceActive = userService.getAutomaticReservationAcceptanceStatus(email);
        return Response
                .ok()
                .entity(new GeneralResponse<>(isAutomaticReservationAcceptanceActive, "Successfully retrieved automatic reservation acceptance status"))
                .build();
    }

    @PATCH
    @Path("/append-cancellation")
    @RolesAllowed("guest")
    public Response appendCancellation(@Context SecurityContext ctx) {
        String email = ctx.getUserPrincipal().getName();
        User user = userService.appendCancellation(email);
        return Response
                .ok()
                .entity(new GeneralResponse<>(user.getNoCancellations(), "Successfully appended number of cancellations"))
                .build();
    }

    @GET
    @Path("/no-cancellations/{guest_email}")
    @RolesAllowed("host")
    public Response getNoCancellations(@Context SecurityContext ctx, @PathParam("guest_email") String guestEmail) {
        int noCancellations = userService.getNoCancellations(guestEmail);
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
        GeneralResponse response = microserviceCommunicator.processResponse(
                "http://localhost:8003/reservation-service/reservation/are-reservations-active/" + email,
                "GET",
                authorizationHeader);
        if (!(boolean) response.getData()) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(new GeneralResponse<>(false, "There are active reservations"))
                    .build();
        }
        userService.deactivateUser(email);
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
        GeneralResponse response = microserviceCommunicator.processResponse(
                "http://localhost:8003/reservation-service/reservation/do-active-reservations-exist/" + email,
                "GET",
                authorizationHeader);
        if ((boolean) response.getData()) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(new GeneralResponse<>(false, "There are active reservations"))
                    .build();
        }
        userService.deactivateUser(email);
        GeneralResponse successfulAccommodationDeletion = microserviceCommunicator.processResponse(
                "http://localhost:8002/accommodation-service/accommodation/deactivate-hosts-accommodations/" + email,
                "PATCH",
                authorizationHeader);
        if (!(boolean) successfulAccommodationDeletion.getData())  {
            return Response
                    .status(Response.Status.OK)
                    .entity(new GeneralResponse<>(false, "Something went wrong while terminating accommodations"))
                    .build();
        }
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
        GeneralResponse response = microserviceCommunicator.processResponse(
                "http://localhost:8003/reservation-service/retrieve-reservation-hosts/" + email,
                "GET",
                "");
        List<String> hostEmails = (List<String>) response.getData();
        List<HostReviewDTO> reviewDTOS = userService.retrieveHostReviews(hostEmails, email);
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
        GeneralResponse response = microserviceCommunicator.processResponse(
                "http://localhost:8003/reservation-service/retrieve-reservation-accommodations/" + email,
                "GET",
                "");
        List<Long> accommodationIds = (List<Long>) response.getData();

        GeneralResponse minAccommodations = microserviceCommunicator.processResponse(
                "http://localhost:8002/accommodation-service/retrieve-min-accommodations",
                "GET",
                "");
        List<MinAccommodationDTO> minAccommodationDTOS = (List<MinAccommodationDTO>) minAccommodations.getData();
        List<AccommodationReviewDTO> accommodationReviews = userService.retrieveAccommodationReviews(email, accommodationIds, minAccommodationDTOS);
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
        HostReview addedReview = userService.addHostReview(email, hostReviewDTO);
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
        AccommodationReview accommodationReview = userService.addAccommodationReview(email, accommodationReviewDTO);
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
        HostReview deletedReview = userService.deleteHostReview(email, hostEmail);
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
        AccommodationReview deletedReview = userService.deleteAccommodationReview(email, accommodationName);
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
        HostReviewInfoDTO hostReviewInfoDTO = userService.getHostReviewsInfo(email);
        return Response
                .ok()
                .entity(new GeneralResponse<>(hostReviewInfoDTO, "Successfully retrieved host reviews info"))
                .build();
    }

    @GET
    @Path("/accommodation-reviews-info/{accommodation_name}")
    @PermitAll
    public Response getAccommodationReviewsInfo(@PathParam("accommodation_name") String accommodationName) {
        AccommodationReviewInfoDTO accommodationReviewInfoDTO = userService.getAccommodationReviewsInfo(accommodationName);
        return Response
                .ok()
                .entity(new GeneralResponse<>(accommodationReviewInfoDTO, "Successfully retrieved accommodation reviews info"))
                .build();
    }

    @GET
    @Path("/avg-rating/{accommodation_name}")
    @PermitAll
    public Response getAvgRating(@PathParam("accommodation_name") String accommodationName) {
        float avgRating = userService.getAvgRating(accommodationName);
        return Response
                .ok()
                .entity(new GeneralResponse<>(avgRating, "Successfully retrieved accommodation rating"))
                .build();
    }

}
