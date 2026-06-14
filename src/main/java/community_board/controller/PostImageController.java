package community_board.controller;

import community_board.dto.image.post.PostImageFileResponse;
import community_board.dto.image.post.PostImageResponse;
import community_board.dto.image.post.PostImageUrlResponse;
import community_board.dto.image.profile.PostProfileImageRequest;
import community_board.global.response.ApiResponse;
import community_board.service.PostImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/posts/{postId}/images")
    public ResponseEntity<?> getPostImages(@PathVariable Integer postId) {
        List<PostImageUrlResponse> response = postImageService.getPostImages(postId);
        return ResponseEntity.ok(ApiResponse.of("POST_IMAGE_SUCCESS", response));
    }

    //게시글 이미지 타입별 실제 파일 조회
    @GetMapping("/posts/{postId}/images/{postImageId}/file")
    public ResponseEntity<Resource> getPostImageFile(
            @PathVariable Integer postId,
            @PathVariable Integer postImageId,
            @RequestParam String type
    ) {
        PostImageFileResponse response = postImageService.getPostImageFile(postId, postImageId, type);
        return ResponseEntity.ok()
                .contentType(response.getMediaType())
                .body(response.getResource());
    }

    @PutMapping("/posts/{postId}/images")
    public ResponseEntity<?> updatePostImages(
            @PathVariable Integer postId,
            @AuthenticationPrincipal Integer loginUserId,
            @RequestParam(value = "files", required = false) List<MultipartFile> files
    ) {
        if (files == null || files.isEmpty()) {
            postImageService.deletePostImages(postId, loginUserId);
            return ResponseEntity.noContent().build();
        }
        List<PostProfileImageRequest> requests = files.stream()
                .map(PostProfileImageRequest::new)
                .toList();
        List<PostImageUrlResponse> response = postImageService.updatePostImages(postId, loginUserId, requests);
        return ResponseEntity.ok(ApiResponse.of("POST_IMAGE_UPDATED", response));
    }
}
