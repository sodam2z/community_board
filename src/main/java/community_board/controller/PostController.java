package community_board.controller;

import community_board.dto.post.*;
import community_board.global.response.ApiResponse;
import community_board.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController//JSON 형식으로 반환
public class PostController {
    private final PostService postService;

    @PostMapping("/posts")
    public ResponseEntity<?> post(@RequestBody CreatePostRequest request) {
        Integer userId = (Integer)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CreatePostResponse response = postService.save(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of("POST_CREATED", response));
    }

    @GetMapping("/posts/{postId}")
    //url 경로에서 값 추출
    public ResponseEntity<?> findPost(@PathVariable Integer postId) {
        GetPostResponse response = postService.findById(postId);
        return ResponseEntity.ok().body(ApiResponse.of("POST_VIEW_SUCCESS", response));
    }

    @PatchMapping("/posts/{postId}")
    public ResponseEntity<?> updatePost(@PathVariable Integer postId, @RequestBody UpdatePostRequest request) {
        UpdatePostResponse response = postService.update(postId, request);
        return ResponseEntity.ok().body(ApiResponse.of("POST_MODIFIED", response));
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Integer postId) {
        postService.deleteById(postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/posts")
    public ResponseEntity<?> getPosts(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {
        Pageable pageable = PageRequest.of(offset, limit);
        return ResponseEntity.ok().body(ApiResponse.of("POST_LIST_SUCCESS", postService.findPostList(pageable)));
    }

}
