package community_board.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateUserRequest {
    @NotBlank(message = "{user.nickname.required}")
    @Size(max = 10, message = "{user.nickname.size}")
    private String nickname;
}
