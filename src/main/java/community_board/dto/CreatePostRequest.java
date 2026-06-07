package community_board.dto;

import community_board.domain.Post;
import community_board.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor //기본 생성자 추가
@AllArgsConstructor // 모든 필드 값을 파라미터로 받는 생성자
@Getter
public class CreatePostRequest {
    private String title;
    private String content;

    //생성자를 사용하여 객체 생성
    public Post toEntity(User user) {
        return Post.builder()
                .title(title)
                .content(content)
                .user(user)
                .build();
    }
}
