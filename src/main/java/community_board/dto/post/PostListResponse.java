package community_board.dto.post;

import community_board.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PostListResponse {
    private Integer postId;
    private String title;
    private LocalDateTime updatedAt;
    private int views;
    private String nickname;

    public static PostListResponse from(Post post) {
        return new PostListResponse(
                post.getPostId(),
                post.getTitle(),
                post.getUpdatedAt(),
                post.getViews(),
                post.getUser().getNickname()
        );
    }
}
