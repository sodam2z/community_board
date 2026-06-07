package community_board.dto.post;

import community_board.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor //기본 생성자 추가
@AllArgsConstructor // 모든 필드 값을 파라미터로 받는 생성자
@Getter
public class CreatePostResponse {
    private Integer postId;
    private String title;
    private String content;
    private LocalDateTime createdAt;

    public static CreatePostResponse from(Post post) {
        return new CreatePostResponse(post.getPostId(), post.getTitle(), post.getContent(), post.getCreatedAt());
    }

}
