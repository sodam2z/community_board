package community_board.dto.image.profile;

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
}
