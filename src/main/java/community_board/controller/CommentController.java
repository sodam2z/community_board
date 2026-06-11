package community_board.controller;

import community_board.dto.comment.CreateCommentRequest;
import community_board.dto.comment.CreateCommentResponse;
import community_board.dto.comment.UpdateCommentRequest;
import community_board.dto.comment.UpdateCommentResponse;
import community_board.global.response.ApiResponse;
import community_board.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<?> comment(
            @PathVariable Integer postId,
            @AuthenticationPrincipal Integer loginUserId,
            @Valid @RequestBody CreateCommentRequest request) {
        CreateCommentResponse response = commentService.save(postId, loginUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of("COMMENT_CREATED", response));
    }

    @PutMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<?> updateComment(
            @PathVariable Integer commentId,
            @AuthenticationPrincipal Integer loginUserId,
            @Valid @RequestBody UpdateCommentRequest request){
        UpdateCommentResponse response = commentService.update(commentId, loginUserId, request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.of("COMMENT_UPDATED", response));
    }

    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Integer commentId,
            @AuthenticationPrincipal Integer loginUserId){
        commentService.deleteById(commentId, loginUserId);
        return ResponseEntity.noContent().build();
    }

}
