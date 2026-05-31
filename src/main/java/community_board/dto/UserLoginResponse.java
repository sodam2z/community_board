package community_board.dto;

public class UserLoginResponse {

    private final Integer userId;
    private final String nickname;
    private final String profileImage;

    public UserLoginResponse(Integer userId, String nickname, String profileImage) {
        this.userId = userId;
        this.nickname = nickname;
        this.profileImage = profileImage;
    }

    public Integer getUserId() { return userId; }
    public String getNickname() { return nickname; }
    public String getProfileImage() { return profileImage; }
}
