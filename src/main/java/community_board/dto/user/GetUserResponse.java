package community_board.dto.user;

import community_board.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GetUserResponse {
    private Integer userId;
    private String email;
    private String nickname;

    public static GetUserResponse from(User user) {
        return new GetUserResponse(
                user.getUserId(),
                user.getEmail(),
                user.getNickname()
        );
    }
}
