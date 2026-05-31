package community_board.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    //회원가입 중복 오류
    EMAIL_DUPLICATED(HttpStatus.CONFLICT, "EMAIL_DUPLICATED"),
    NICKNAME_DUPLICATED(HttpStatus.CONFLICT, "NICKNAME_DUPLICATED"),
    //회원가입 입력값 정책 위반
    EMAIL_POLICY_VIOLATION(HttpStatus.UNPROCESSABLE_ENTITY, "EMAIL_POLICY_VIOLATION"),
    PASSWORD_POLICY_VIOLATION(HttpStatus.UNPROCESSABLE_ENTITY, "PASSWORD_POLICY_VIOLATION"),
    NICKNAME_POLICY_VIOLATION(HttpStatus.UNPROCESSABLE_ENTITY, "NICKNAME_POLICY_VIOLATION"),
    //필수값 누락
    REQUIRED_MISSING(HttpStatus.BAD_REQUEST, "REQUIRED_MISSING"),
    //로그인 실패
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "LOGIN_FAILED"),
    //서버 오류
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_ERROR");

    private final HttpStatus httpStatus;
    private final String code;

    ErrorCode (HttpStatus httpStatus, String code){
        this.httpStatus = httpStatus;
        this.code = code;
    }
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
    public String getCode() {
        return code;
    }
}
