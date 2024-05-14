package uns.ac.rs.controller;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import uns.ac.rs.GeneralResponse;
import uns.ac.rs.dto.LocationDTO;
import uns.ac.rs.model.Location;
import uns.ac.rs.service.LocationService;

import java.util.ArrayList;

@Path("/location")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LocationController {

    @Autowired
    private LocationService locationService;

    @GET
    @Path("/get")
    @PermitAll
    public Response getAllLocations() {
        ArrayList<Location> locations = locationService.getAll();
        ArrayList<LocationDTO> locationsDTO = new ArrayList<>();
        for (Location location: locations) {
            locationsDTO.add(new LocationDTO(location));
        }
        return Response.ok(new GeneralResponse<>(locationsDTO, "Successfully retrieved locations")).build();
    }



}
