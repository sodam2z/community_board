package community_board.controller;

import community_board.dto.like.GetPostLikeResponse;
import community_board.dto.like.PostLikeResponse;
import community_board.global.response.ApiResponse;
import community_board.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/posts/{postId}/likes")
    public ResponseEntity<ApiResponse<PostLikeResponse>> like(
            @PathVariable Integer postId,
            @AuthenticationPrincipal Integer loginUserId) {
        PostLikeResponse response = likeService.save(postId, loginUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of("LIKE_CREATED", response));
    }

    @GetMapping("/posts/{postId}/likes")
    public ResponseEntity<ApiResponse<GetPostLikeResponse>> getLike(
            @PathVariable Integer postId,
            @AuthenticationPrincipal Integer loginUserId) {
        GetPostLikeResponse response = likeService.findByPostId(postId, loginUserId);
        return ResponseEntity.ok().body(ApiResponse.of("LIKE_VIEW_SUCCESS", response));
    }

    @DeleteMapping("/posts/{postId}/likes")
    public ResponseEntity<ApiResponse<PostLikeResponse>> unlike(
            @PathVariable Integer postId,
            @AuthenticationPrincipal Integer loginUserId) {
        PostLikeResponse response = likeService.delete(postId, loginUserId);
        return ResponseEntity.ok().body(ApiResponse.of("LIKE_DELETED", response));
    }


}
