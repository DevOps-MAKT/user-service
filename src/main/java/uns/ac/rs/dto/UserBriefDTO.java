package uns.ac.rs.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

@Data
@RegisterForReflection
public class UserBriefDTO {
    private String username;
    private String firstName;
    private String lastName;

    public UserBriefDTO() {
    }

    public UserBriefDTO(String username, String firstName, String lastName) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
