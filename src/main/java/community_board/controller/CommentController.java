package community_board.controller;

import community_board.dto.comment.CreateCommentRequest;
import community_board.dto.comment.CreateCommentResponse;
import community_board.global.response.ApiResponse;
import community_board.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<?> comments(
            @PathVariable Integer postId,
            @AuthenticationPrincipal Integer loginUserId,
            @Valid @RequestBody CreateCommentRequest request) {
        CreateCommentResponse response = commentService.save(postId, loginUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of("COMMENT_CREATED", response));
    }

}
