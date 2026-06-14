package community_board.dto.image.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ProfileImageUrlResponse {
    private String jpgUrl;
    private String webpUrl;
    private String thumbnailUrl;

    //프로필 이미지 타입별 조회 URL 반환
    public static ProfileImageUrlResponse of(String jpgUrl, String webpUrl, String thumbnailUrl) {
        return new ProfileImageUrlResponse(jpgUrl, webpUrl, thumbnailUrl);
    }
}
