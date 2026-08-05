package community_board.controller;

import community_board.dto.user.UserLoginRequest;
import community_board.dto.user.UserLoginResponse;
import community_board.global.response.ApiResponse;
import community_board.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    // 로그인 POST /auth
    @PostMapping
    public ResponseEntity<ApiResponse<UserLoginResponse>> login(
            @Valid @RequestBody UserLoginRequest request,
            HttpServletResponse response
    ) {
        UserLoginResponse loginResponse = authService.login(request, response);
        return ResponseEntity.ok(ApiResponse.of("USER_LOGIN", loginResponse));
    }

    // 로그아웃 DELETE /auth
    @DeleteMapping
    public ResponseEntity<Void> logout(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        authService.logout(refreshToken, response);
        return ResponseEntity.noContent().build();
    }
}
