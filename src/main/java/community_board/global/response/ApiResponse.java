package community_board.global.response;

public class ApiResponse<T> {
    private final String code;
    private final T data;
    public ApiResponse(String code, T data) {
        this.code = code;
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public T getData() {
        return data;
    }

    public static <T> ApiResponse<T> of (String code, T data) {
        return new ApiResponse<>(code, data);
    }

    public static ApiResponse<Void> of (String code){
        return new ApiResponse<>(code, null);
    }
}
