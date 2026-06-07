package community_board.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAccessTokenRequest {
    @NotBlank(message = "리프레시 토큰은 필수 입력 값입니다.")
    private String refreshToken;
}
