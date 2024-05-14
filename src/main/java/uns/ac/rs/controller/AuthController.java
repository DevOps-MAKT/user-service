package uns.ac.rs.controller;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import uns.ac.rs.GeneralResponse;
import uns.ac.rs.dto.LoginDTO;
import uns.ac.rs.model.User;
import uns.ac.rs.service.AuthService;

import java.util.Optional;

@Path("/auth")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthController {

    @Autowired
    private AuthService authService;

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginDTO loginDTO) {
        Optional<User> user = authService.login(loginDTO);
        if (user.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new GeneralResponse<>("", "Credentials aren't valid"))
                    .build();
        }
        else {
            String jwt = authService.generateJwt(user.get());
            return Response.ok(new GeneralResponse<>(jwt, "Successfully logged in"))
                    .build();
        }
    }

}
