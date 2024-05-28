package uns.ac.rs.dto;

import lombok.Data;

@Data
public class PasswordDTO {
    private String password;
    private String confirmationPassword;

    public PasswordDTO() {

    }
}
