package community_board.controller;

import community_board.dto.image.profile.PostProfileImageRequest;
import community_board.dto.image.profile.ProfileImageFileResponse;
import community_board.dto.image.profile.ProfileImageResponse;
import community_board.dto.image.profile.ProfileImageUrlResponse;
import community_board.global.response.ApiResponse;
import community_board.service.ProfileImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
public class ProfileImageController {
    private final ProfileImageService profileImageService;

    @PostMapping("/images/profile")
    public ResponseEntity<?> uploadProfileImage(@RequestParam("file") MultipartFile file) {
        PostProfileImageRequest request = new PostProfileImageRequest(file);
        ProfileImageResponse response = profileImageService.uploadProfileImage(request);
        return ResponseEntity.ok(ApiResponse.of("PROFILE_IMAGE_UPLOADED", response));
    }
    @GetMapping("/users/{userId}/profile-image")
    public ResponseEntity<?> getProfileImage
            (@PathVariable Integer userId)
    {
        ProfileImageUrlResponse response = profileImageService.getProfileImage(userId);
        return ResponseEntity.ok(ApiResponse.of("PROFILE_IMAGE_SUCCESS", response));

    }

    //프로필 이미지 타입별 실제 파일 조회
    @GetMapping("/users/{userId}/profile-image/file")
    public ResponseEntity<Resource> getProfileImageFile(
            @PathVariable Integer userId,
            @RequestParam String type
    ) {
        ProfileImageFileResponse response = profileImageService.getProfileImageFile(userId, type);
        return ResponseEntity.ok()
                .contentType(response.getMediaType())
                .body(response.getResource());
    }

    @PutMapping("/users/{userId}/profile-image")
    public ResponseEntity<?> updateProfileImage(
            @PathVariable Integer userId,
            @AuthenticationPrincipal Integer loginUserId,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        if (file == null || file.isEmpty()) {
            profileImageService.deleteProfileImage(userId, loginUserId);
            return ResponseEntity.noContent().build();
        }
        PostProfileImageRequest request = new PostProfileImageRequest(file);
        ProfileImageUrlResponse response = profileImageService.updateProfileImage(userId, loginUserId, request);
        return ResponseEntity.ok(ApiResponse.of("PROFILE_IMAGE_MODIFIED", response));
    }
}
