package community_board.dto.comment;

import community_board.domain.PostComment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CreateCommentResponse {
    private Integer commentId;
    private String nickname;
    private String content;
    private LocalDateTime createdAt;

    public static CreateCommentResponse from(PostComment comment) {
        return new CreateCommentResponse(comment.getCommentId(), comment.getUser().getNickname(), comment.getContent(), comment.getCreatedAt());
    }
}
