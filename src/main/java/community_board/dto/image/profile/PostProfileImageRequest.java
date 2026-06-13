package community_board.dto.image.profile;

import community_board.global.exception.ImageErrorCode;
import community_board.global.exception.RestApiException;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostProfileImageRequest {
    @NotNull(message = "{image.file.required}")
    private MultipartFile file;

    //파일 크기 검증
    public boolean isFileSizeValid(long maxSize) {
        return file != null && file.getSize() <= maxSize;
    }

    //확장자 검증
    public boolean isFileExtensionValid() {
        if (file == null || file.getOriginalFilename() == null) return false;
        String extension = file.getOriginalFilename()
                .substring(file.getOriginalFilename().lastIndexOf(".") + 1)
                .toLowerCase();
        return List.of("jpg", "jpeg", "png").contains(extension);
    }

    // 파일 검증 통합 메서드
    public void validate(long maxSize) {
        if (!isFileSizeValid(maxSize)) {
            throw new RestApiException(ImageErrorCode.IMAGE_SIZE_EXCEEDED);
        }
        if (!isFileExtensionValid()) {
            throw new RestApiException(ImageErrorCode.IMAGE_INVALID_EXTENSION);
        }
    }
}
