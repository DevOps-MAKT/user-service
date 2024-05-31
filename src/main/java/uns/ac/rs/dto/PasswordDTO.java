package uns.ac.rs.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

@Data
@RegisterForReflection
public class PasswordDTO {
    private String password;
    private String confirmationPassword;

    public PasswordDTO() {

    }
}
