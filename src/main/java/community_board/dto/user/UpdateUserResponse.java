package community_board.dto.user;

import community_board.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateUserResponse {
    private String nickname;

    public static UpdateUserResponse from(User user) {
        return new UpdateUserResponse(
                user.getNickname()
        );
    }
}
