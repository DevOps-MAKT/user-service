package uns.ac.rs.service;

import io.quarkus.elytron.security.common.BcryptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uns.ac.rs.dto.LoginDTO;
import uns.ac.rs.model.User;
import uns.ac.rs.repository.UserRepository;

import io.smallrye.jwt.build.Jwt;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public Optional<User> login(LoginDTO loginDTO) {
        User user = userRepository.findByUsername(loginDTO.getUsername());
        if (user == null || !BcryptUtil.matches(loginDTO.getPassword(), user.getPassword())) {
            return Optional.empty();
        }
        return Optional.of(user);
    }

    public String generateJwt(User user) {
        long duration = System.currentTimeMillis() / 1000 + 3600;
        return Jwt.issuer("user-service")
                .subject(user.getUsername())
                .groups(user.getRole())
                .expiresAt(duration)
                .sign();
    }

}
