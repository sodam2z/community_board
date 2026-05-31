package community_board.global.exception;

import community_board.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//전체 Controller에서 발생하는 예외 공통 처리
@RestControllerAdvice
public class GlobalExceptionHandler {
    //BusinessException 처리
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getHttpStatus()).body(ApiResponse.of(errorCode.getCode()));
    }
    //@Valid 실패 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String field = e.getBindingResult().getFieldError().getField();

        if ("email".equals(field)) {
            return ResponseEntity.status(ErrorCode.EMAIL_POLICY_VIOLATION.getHttpStatus()).body(ApiResponse.of(ErrorCode.EMAIL_POLICY_VIOLATION.getCode()));
        }
        if ("password".equals(field)) {
            return ResponseEntity.status(ErrorCode.PASSWORD_POLICY_VIOLATION.getHttpStatus()).body(ApiResponse.of(ErrorCode.PASSWORD_POLICY_VIOLATION.getCode()));
        }
        if ("nickname".equals(field)) {
            return ResponseEntity.status(ErrorCode.NICKNAME_POLICY_VIOLATION.getHttpStatus()).body(ApiResponse.of(ErrorCode.NICKNAME_POLICY_VIOLATION.getCode()));
        }

        return ResponseEntity.status(ErrorCode.REQUIRED_MISSING.getHttpStatus()).body(ApiResponse.of(ErrorCode.REQUIRED_MISSING.getCode()));

    }
    //서버 오류 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        return ResponseEntity.status(ErrorCode.SERVER_ERROR.getHttpStatus()).body(ApiResponse.of(ErrorCode.SERVER_ERROR.getCode()));
    }
}