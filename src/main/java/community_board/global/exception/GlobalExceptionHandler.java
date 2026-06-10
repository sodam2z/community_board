package community_board.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // RestApiException 처리
    @ExceptionHandler(RestApiException.class)
    public ResponseEntity<Object> handleCustomException(RestApiException e) {
        ErrorCode errorCode = e.getErrorCode();
        return handleExceptionInternal(errorCode);
    }

    // IllegalArgumentException 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("handleIllegalArgument", e);
        return handleExceptionInternal(CommonErrorCode.REQUIRED_MISSING, e.getMessage());
    }

    // @Valid 실패 처리
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        List<ErrorResponse.ValidationError> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(ErrorResponse.ValidationError::of)
                .toList();

        ErrorResponse response = ErrorResponse.builder()
                .code(CommonErrorCode.VALIDATION_FAILED.name())
                .message(CommonErrorCode.VALIDATION_FAILED.getMessage())
                .errors(errors)
                .build();

        return ResponseEntity
                .status(CommonErrorCode.VALIDATION_FAILED.getHttpStatus())
                .body(response);
    }

    // 공통 응답 생성 — ErrorCode만 받는 경우
    private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode) {
        ErrorResponse response = makeErrorResponse(errorCode);
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(response);
    }

    // 공통 응답 생성 — ErrorCode + 메시지 override
    private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode, String message) {
        ErrorResponse response = makeErrorResponse(errorCode, message);
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(response);
    }

    private ErrorResponse makeErrorResponse(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .errors(List.of())
                .build();
    }

    private ErrorResponse makeErrorResponse(ErrorCode errorCode, String message) {
        return ErrorResponse.builder()
                .code(errorCode.name())
                .message(message)
                .errors(List.of())
                .build();
    }
}