package community_board.controller;

import community_board.dto.image.profile.PostProfileImageRequest;
import community_board.dto.image.profile.PostProfileImageResponse;
import community_board.global.response.ApiResponse;
import community_board.service.ProfileImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
public class ProfileImageController {
    private final ProfileImageService profileImageService;

    @PostMapping("/images/profile")
    public ResponseEntity<?> uploadProfileImage(@RequestParam("file") MultipartFile file) {
        PostProfileImageRequest request = new PostProfileImageRequest(file);
        PostProfileImageResponse response = profileImageService.uploadProfileImage(request);
        return ResponseEntity.ok(ApiResponse.of("PROFILE_IMAGE_UPLOADED", response));
    }
}
