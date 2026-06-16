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
public class GetPostResponse {
    private Integer postId;
    private String title;
    private String content;
    private Integer userId;
    private String nickname;
    private String profileImageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int views;

    public static GetPostResponse from(Post post) {
        Integer userId = post.getUser().getUserId();

        return new GetPostResponse(
                post.getPostId(),
                post.getTitle(),
                post.getContent(),
                userId,
                post.getUser().getNickname(),
                createProfileImageUrl(userId),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                post.getViews()
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
