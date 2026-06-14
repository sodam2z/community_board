package community_board.dto.image.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

@AllArgsConstructor
@Getter
public class PostImageFileResponse {
    private Resource resource;
    private MediaType mediaType;

    //실제 이미지 파일과 파일 형식을 함께 반환
    public static PostImageFileResponse of(Resource resource, MediaType mediaType) {
        return new PostImageFileResponse(resource, mediaType);
    }
}
