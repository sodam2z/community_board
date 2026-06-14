package community_board.dto.image.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

@AllArgsConstructor
@Getter
public class ProfileImageFileResponse {
    private Resource resource;
    private MediaType mediaType;

    //실제 이미지 파일과 파일 형식을 함께 반환
    public static ProfileImageFileResponse of(Resource resource, MediaType mediaType) {
        return new ProfileImageFileResponse(resource, mediaType);
    }
}
