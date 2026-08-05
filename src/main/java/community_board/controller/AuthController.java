package community_board.controller;

import community_board.dto.user.UserLoginRequest;
import community_board.dto.user.UserLoginResponse;
import community_board.global.response.ApiResponse;
import community_board.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
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
            //쿠키 Secure 여부를 현재 요청 기준으로 판단하려고 같이 넘긴다.
            HttpServletRequest httpRequest,
            HttpServletResponse response
    ) {
        UserLoginResponse loginResponse = authService.login(request, httpRequest, response);
        return ResponseEntity.ok(ApiResponse.of("USER_LOGIN", loginResponse));
    }

    // 로그아웃 DELETE /auth
    @DeleteMapping
    public ResponseEntity<Void> logout(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            //삭제 쿠키도 발급 쿠키와 같은 보안 속성으로 맞춰서 내려준다.
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        authService.logout(refreshToken, request, response);
        return ResponseEntity.noContent().build();
    }
}
