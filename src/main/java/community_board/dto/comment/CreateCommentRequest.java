package community_board.dto.comment;

import community_board.domain.Post;
import community_board.domain.PostComment;
import community_board.domain.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor //기본 생성자 추가
@AllArgsConstructor // 모든 필드 값을 파라미터로 받는 생성자
@Getter
public class CreateCommentRequest {
    @NotBlank(message = "{comment.content.required}")
    @Size(max = 255, message = "{comment.content.size}")
    private String content;

    //생성자를 사용하여 객체 생성
    public PostComment toEntity(User user, Post post) {
        return PostComment.builder()
                .content(content)
                .user(user)
                .post(post)
                .build();
    }
}
