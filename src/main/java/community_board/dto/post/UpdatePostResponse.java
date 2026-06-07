package community_board.dto.post;

import community_board.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdatePostResponse {
    private String title;
    private String content;
    private Integer postId;
    private LocalDateTime updatedAt;
    private String nickname;

    public static UpdatePostResponse from(Post post) {
        return new UpdatePostResponse(
                post.getTitle(),
                post.getContent(),
                post.getPostId(),
                post.getUpdatedAt(),
                post.getUser().getNickname()
        );
    }

}
