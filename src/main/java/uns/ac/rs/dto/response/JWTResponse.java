package uns.ac.rs.dto.response;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

@Data
@RegisterForReflection
public class JWTResponse {
    private String jwt;
    private String role;
    private String email;

    public JWTResponse(String jwt, String role, String email) {
        this.jwt = jwt;
        this.role = role;
        this.email = email;
    }

    public JWTResponse() {
    }
}
