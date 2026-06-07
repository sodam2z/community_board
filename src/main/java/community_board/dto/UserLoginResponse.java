package community_board.dto;

public class UserLoginResponse {

    private final Integer userId;
    private final String nickname;

    public UserLoginResponse(Integer userId, String nickname) {
        this.userId = userId;
        this.nickname = nickname;
    }

    public Integer getUserId() { return userId; }
    public String getNickname() { return nickname; }
}
