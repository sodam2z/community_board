package community_board.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdatePasswordRequest {
    @NotBlank(message = "{user.password.required}")
    @Size(min = 8, max = 20, message = "{user.password.size}")
    private String password;

    @NotBlank(message = "{user.password.required}")
    private String confirmPassword;
}

