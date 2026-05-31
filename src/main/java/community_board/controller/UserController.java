package community_board.controller;

import community_board.global.response.ApiResponse;
import community_board.dto.UserSignupRequest;
import community_board.dto.UserSignupResponse;
import community_board.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    //생성자 주입
    public UserController(UserService userService) {
        this.userService = userService;
    }

    //회원가입 API
    @PostMapping
    public ResponseEntity<ApiResponse<UserSignupResponse>> signup(@Valid @RequestBody UserSignupRequest request) {
        UserSignupResponse response = userService.signup(request);

        return ResponseEntity.ok(ApiResponse.of("USER_CREATED",response));
    }
}
