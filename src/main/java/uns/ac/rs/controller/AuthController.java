package uns.ac.rs.controller;

import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import uns.ac.rs.GeneralResponse;
import uns.ac.rs.dto.LoginDTO;
import uns.ac.rs.dto.response.JWTResponse;
import uns.ac.rs.model.User;
import uns.ac.rs.service.AuthService;

import java.util.Optional;

@Path("/auth")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    private AuthService authService;

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginDTO loginDTO) {
        Optional<User> user = authService.login(loginDTO);
        if (user.isEmpty()) {
            logger.warn("Login failed for {}", loginDTO.getUsername());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new GeneralResponse<>("", "Credentials aren't valid or the user has been deleted"))
                    .build();
        }
        else {
            String jwt = authService.generateJwt(user.get());
            logger.info("Login successful for {}", loginDTO.getUsername());
            return Response.ok(new GeneralResponse<>(new JWTResponse(jwt, user.get().getRole()), "Successfully logged in"))
                    .build();
        }
    }

    @GET
    @Path("/authorize/{role}")
    @PermitAll
    /*
     * Use this endpoint to authorize requests from other microservices.
     * Pass the role that the currently logged-in user should have.
     * If the request is authorized, the user email will be sent to the microservice from which the authorization is being requested.
     */
    public Response authorize(@Context SecurityContext ctx, @PathParam("role") String role) {
        if (ctx.getUserPrincipal() == null) {
            logger.warn("Unauthorized access for role {}", role);
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new GeneralResponse<>("", "Unauthorized access - no one is logged in"))
                    .build();
        }
        String email = ctx.getUserPrincipal().getName();
        try {
            User user = authService.validateUserWithRole(email, role);
            logger.info("Authorized access for user {}", email);
            return Response.ok(new GeneralResponse<>(user.getEmail(), "Authorization successful"))
                    .build();
        } catch (Exception e) {
            logger.warn("Unauthorized access for {}", email);
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new GeneralResponse<>("", "Unauthorized access - user does not have the adequate role"))
                    .build();
        }
    }

}
