package community_board.dto.image.profile;

import community_board.domain.ProfileImage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PostProfileImageResponse {
    private String jpgPath;
    private String webpPath;
    private String thumbnailPath;

    // DB 저장 없이 경로만 반환할 때 사용
    public static PostProfileImageResponse of(String jpgPath, String webpPath, String thumbnailPath) {
        return new PostProfileImageResponse(jpgPath, webpPath, thumbnailPath);
    }

    public static PostProfileImageResponse from(ProfileImage profileImage) {
        return new PostProfileImageResponse(
                profileImage.getJpgPath(),
                profileImage.getWebpPath(),
                profileImage.getThumbnailPath()
        );
    }
}
