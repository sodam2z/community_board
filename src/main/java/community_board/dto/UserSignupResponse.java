package community_board.dto;

public class UserSignupResponse {
    private final Integer userId;
    private final String email;
    private final String nickname;
    private final String profileImage;

    public UserSignupResponse(Integer userId, String email, String nickname, String profileImage) {
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
        this.profileImage = profileImage;
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

    public String getProfileImage() {
        return profileImage;
    }

}
