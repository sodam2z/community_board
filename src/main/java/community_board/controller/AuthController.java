package community_board.controller;

import community_board.dto.UserLoginRequest;
import community_board.dto.UserLoginResponse;
import community_board.global.response.ApiResponse;
import community_board.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    //생성자 주입
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    //로그인 API
    @PostMapping
    public ResponseEntity<ApiResponse<UserLoginResponse>> login(@Valid @RequestBody UserLoginRequest request) {
        UserLoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.of("USER_LOGIN", response));
    }
}
