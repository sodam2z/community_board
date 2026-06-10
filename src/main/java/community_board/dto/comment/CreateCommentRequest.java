package community_board.dto.comment;

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
}
