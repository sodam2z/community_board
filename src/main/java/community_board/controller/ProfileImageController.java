package community_board.controller;

import community_board.dto.image.profile.PostProfileImageRequest;
import community_board.dto.image.profile.ProfileImageResponse;
import community_board.global.response.ApiResponse;
import community_board.service.ProfileImageService;
import lombok.RequiredArgsConstructor;
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
            (@PathVariable Integer userId,
             @AuthenticationPrincipal Integer loginUserId)
    {
        ProfileImageResponse response = profileImageService.getProfileImage(userId, loginUserId);
        return ResponseEntity.ok(ApiResponse.of("PROFILE_IMAGE_SUCCESS", response));

    }

    @PutMapping("/users/{userId}/profile-image")
    public ResponseEntity<?> updateProfileImage(@PathVariable Integer userId, @AuthenticationPrincipal Integer loginUserId, @RequestParam("file") MultipartFile file) {
        PostProfileImageRequest request = new PostProfileImageRequest(file);
        ProfileImageResponse response = profileImageService.updateProfileImage(userId, loginUserId, request);
        return ResponseEntity.ok(ApiResponse.of("PROFILE_IMAGE_MODIFIED", response));
    }
}
