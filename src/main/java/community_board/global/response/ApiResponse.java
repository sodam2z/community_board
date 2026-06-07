package community_board.global.response;

import lombok.Getter;

@Getter
public class ApiResponse<T> {
    private final String code;
    private final T data;

    private ApiResponse(String code, T data) {
        this.code = code;
        this.data = data;
    }

    //데이터 있는 성공 응답
    public static <T> ApiResponse<T> of(String code, T data) {
        return new ApiResponse<>(code, data);
    }

    //데이터가 없는 성공 응답
    public static <T> ApiResponse<T> of(String code) {
        return new ApiResponse<>(code, null);
    }
}
