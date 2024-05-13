package uns.ac.rs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uns.ac.rs.model.Location;
import uns.ac.rs.repository.LocationRepository;

import java.util.ArrayList;

@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    public ArrayList<Location> getAll() {
        return (ArrayList<Location>) locationRepository.listAll();
    }
}
