package community_board.dto.comment;

import community_board.domain.PostComment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class GetCommentResponse {
    private Integer commentId;
    private Integer userId;
    private String nickname;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static GetCommentResponse from(PostComment comment) {
        return new GetCommentResponse(
                comment.getCommentId(),
                comment.getUser().getUserId(),
                comment.getUser().getNickname(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}
