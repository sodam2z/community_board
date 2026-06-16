package community_board.dto.post;

import community_board.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PostListResponse {
    private Integer postId;
    private String title;
    private LocalDateTime updatedAt;
    private int views;
    private Long commentCount;
    private Long likeCount;
    private Integer userId;
    private String nickname;
    private String profileImageUrl;

    public static PostListResponse from(Post post) {
        return from(post, 0L, 0L);
    }

    public static PostListResponse from(Post post, Long commentCount, Long likeCount) {
        Integer userId = post.getUser().getUserId();

        return new PostListResponse(
                post.getPostId(),
                post.getTitle(),
                post.getUpdatedAt(),
                post.getViews(),
                commentCount,
                likeCount,
                userId,
                post.getUser().getNickname(),
                createProfileImageUrl(userId)
        );
    }

    private static String createProfileImageUrl(Integer userId) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/users/{userId}/profile-image/file")
                .queryParam("type", "thumbnail")
                .buildAndExpand(userId)
                .toUriString();
    }
}
