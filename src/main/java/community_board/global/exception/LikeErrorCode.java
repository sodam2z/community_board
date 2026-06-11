package community_board.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum LikeErrorCode implements ErrorCode {
    ALREADY_LIKED(HttpStatus.CONFLICT,"이미 좋아요를 누른 게시글입니다."),
    LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "좋아요를 누르지 않은 게시글입니다."),
    ;
    private final HttpStatus httpStatus;
    private final String message;
}
