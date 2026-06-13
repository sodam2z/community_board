package community_board.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ImageErrorCode implements ErrorCode {
    IMAGE_INVALID_EXTENSION(HttpStatus.UNPROCESSABLE_ENTITY, "지원하지 않는 확장자입니다."),
    IMAGE_SIZE_EXCEEDED(HttpStatus.UNPROCESSABLE_ENTITY, "파일 크기가 10MB를 초과했습니다."),
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "이미지를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
