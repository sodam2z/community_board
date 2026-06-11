package community_board.dto.comment;

import community_board.domain.PostComment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateCommentResponse {
    private Integer commentId;
    private String nickname;
    private String content;
    private LocalDateTime updatedAt;

    public static UpdateCommentResponse from(PostComment comment) {
        return new UpdateCommentResponse(comment.getCommentId(), comment.getUser().getNickname(), comment.getContent(), comment.getUpdatedAt());
    }
}
