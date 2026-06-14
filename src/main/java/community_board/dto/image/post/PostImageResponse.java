package community_board.dto.image.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostImageResponse {
    private String jpgPath;
    private String webpPath;

    public static PostImageResponse of(String jpgPath, String webpPath) {
        return new PostImageResponse(jpgPath, webpPath);
    }
}
