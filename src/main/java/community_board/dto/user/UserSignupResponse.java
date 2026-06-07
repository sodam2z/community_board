package community_board.dto.user;

public class UserSignupResponse {
    private final Integer userId;
    private final String email;
    private final String nickname;

    public UserSignupResponse(Integer userId, String email, String nickname ) {
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getNickname() {
        return nickname;
    }

}
