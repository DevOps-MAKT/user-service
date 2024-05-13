package uns.ac.rs.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import uns.ac.rs.model.User;
import uns.ac.rs.service.AuthService;

import java.util.List;

@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthController {

    @Autowired
    private AuthService authService;

//    @POST
//    @Path("/login")
//    @Produces(MediaType.APPLICATION_JSON)
//    public void login() {
//        return authService.login();
//    }
}
