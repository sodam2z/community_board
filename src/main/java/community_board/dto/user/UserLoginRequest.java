package community_board.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginRequest {

    @Email(message = "{user.email.invalid}")
    @NotBlank(message = "{user.email.required}")
    private String email;

    @NotBlank(message = "{user.password.required}")
    @Size(min = 8, max = 20, message = "{user.password.size}")
    private String password;

}
