package community_board.controller;

import community_board.domain.Post;
import community_board.dto.CreatePostRequest;
import community_board.dto.CreatePostResponse;
import community_board.global.response.ApiResponse;
import community_board.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController//JSON 형식으로 반환
public class PostController {
    private final PostService postService;

    @PostMapping("/post")
    public ResponseEntity<?> post(@RequestBody CreatePostRequest request) {
        Integer userId = (Integer)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CreatePostResponse response = postService.save(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of("POST_CREATED", response));
    }
}
