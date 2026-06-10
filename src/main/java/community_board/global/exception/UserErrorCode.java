package community_board.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
    PASSWORD_POLICY_VIOLATION(HttpStatus.UNPROCESSABLE_CONTENT, "비밀번호는 8자 이상 20자 이하여야 합니다."),
    NICKNAME_POLICY_VIOLATION(HttpStatus.UNPROCESSABLE_CONTENT, "닉네임은 최대 10자까지 가능합니다."),
    EMAIL_POLICY_VIOLATION(HttpStatus.UNPROCESSABLE_CONTENT, "이메일 형식이 올바르지 않습니다."),
    NICKNAME_DUPLICATED(HttpStatus.CONFLICT,  "이미 사용 중인 닉네임입니다."),
    EMAIL_DUPLICATED(HttpStatus.CONFLICT,  "이미 사용 중인 이메일입니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED,  "이메일 또는 비밀번호가 올바르지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND,"사용자를 찾을 수 없습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다.")
    ;
    private final HttpStatus httpStatus;
    private final String message;
}
