package community_board.dto.image.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PostImageUrlResponse {
    private Integer postImageId;
    private String jpgUrl;
    private String webpUrl;

    //게시글 이미지 타입별 조회 URL 반환
    public static PostImageUrlResponse of(Integer postImageId, String jpgUrl, String webpUrl) {
        return new PostImageUrlResponse(postImageId, jpgUrl, webpUrl);
    }
}
