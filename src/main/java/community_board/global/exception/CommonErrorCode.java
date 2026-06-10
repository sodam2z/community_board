package community_board.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {
    REQUIRED_MISSING(HttpStatus.BAD_REQUEST, "필수 입력값이 누락되었습니다."),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
    UNAUTHENTICATED_ACCESS(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "해당 요청을 수행할 권한이 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),
    ;
    private final HttpStatus httpStatus;
    private final String message;
}
