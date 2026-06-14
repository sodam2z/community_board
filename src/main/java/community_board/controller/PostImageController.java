package community_board.controller;

import community_board.dto.image.post.PostImageResponse;
import community_board.dto.image.profile.PostProfileImageRequest;
import community_board.global.response.ApiResponse;
import community_board.service.PostImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class PostImageController {
    private final PostImageService postImageService;

    @PostMapping("/images/post")
    public ResponseEntity<?> uploadPostImage(@RequestParam("files") List<MultipartFile> files) {
        List<PostProfileImageRequest> requests = files.stream()
                .map(PostProfileImageRequest::new)
                .toList();
        List<PostImageResponse> response = postImageService.uploadPostImages(requests);
        return ResponseEntity.ok(ApiResponse.of("POST_IMAGE_UPLOADED", response));
    }
}