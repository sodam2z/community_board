package community_board.controller;

import community_board.dto.user.UserSignupRequest;
import community_board.dto.user.UserSignupResponse;
import community_board.global.response.ApiResponse;
import community_board.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    //회원가입 API
    @PostMapping
    public ResponseEntity<ApiResponse<UserSignupResponse>> signup(@Valid @RequestBody UserSignupRequest request) {
        UserSignupResponse response = userService.signup(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of("USER_CREATED", response));
    }
}
