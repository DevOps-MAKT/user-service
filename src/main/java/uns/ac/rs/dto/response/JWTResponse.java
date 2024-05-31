package uns.ac.rs.dto.response;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

@Data
@RegisterForReflection
public class JWTResponse {
    private String jwt;
    private String role;

    public JWTResponse(String jwt, String role) {
        this.jwt = jwt;
        this.role = role;
    }

    public JWTResponse() {
    }
}
