package community_board.controller;

import community_board.dto.post.*;
import community_board.global.exception.PostErrorCode;
import community_board.global.exception.RestApiException;
import community_board.global.response.ApiResponse;
import community_board.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController//JSON 형식으로 반환
public class PostController {
    private static final int MAX_PAGE_SIZE = 100;

    private final PostService postService;

    @PostMapping("/posts")
    public ResponseEntity<?> post(
            @AuthenticationPrincipal Integer loginUserId,
            @RequestBody CreatePostRequest request) {
        CreatePostResponse response = postService.save(loginUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of("POST_CREATED", response));
    }

    @GetMapping("/posts/{postId}")
    //url 경로에서 값 추출
    public ResponseEntity<?> findPost(@PathVariable Integer postId) {
        GetPostResponse response = postService.findById(postId);
        return ResponseEntity.ok().body(ApiResponse.of("POST_VIEW_SUCCESS", response));
    }

    @PostMapping("/posts/{postId}/views")
    public ResponseEntity<Void> increasePostView(@PathVariable Integer postId) {
        postService.increaseViewCount(postId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/posts/{postId}")
    public ResponseEntity<?> updatePost(
            @PathVariable Integer postId,
            @AuthenticationPrincipal Integer loginUserId,
            @RequestBody UpdatePostRequest request) {
        UpdatePostResponse response = postService.update(postId, loginUserId, request);
        return ResponseEntity.ok().body(ApiResponse.of("POST_MODIFIED", response));
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<?> deletePost(
            @PathVariable Integer postId,
            @AuthenticationPrincipal Integer loginUserId) {
        postService.deleteById(postId,loginUserId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/posts")
    public ResponseEntity<?> getPosts(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {
        Pageable pageable = createPageable(offset, limit);
        return ResponseEntity.ok().body(ApiResponse.of("POST_LIST_SUCCESS", postService.findPostList(pageable)));
    }

    @GetMapping("/posts/trending")
    public ResponseEntity<?> getTrendingPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = createPageable(page, size);
        return ResponseEntity.ok().body(ApiResponse.of("TRENDING_POST_LIST_SUCCESS", postService.findTrendingPostList(pageable)));
    }

    private Pageable createPageable(int page, int size) {
        if (page < 0 || size <= 0 || size > MAX_PAGE_SIZE) {
            throw new RestApiException(PostErrorCode.INVALID_PAGE_REQUEST);
        }
        return PageRequest.of(page, size);
    }

}
