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
import uns.ac.rs.dto.request.UserRequestDTO;
import uns.ac.rs.dto.response.UserResponseDTO;
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


}
