package community_board.controller;

import community_board.dto.user.*;
import community_board.global.response.ApiResponse;
import community_board.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    //회원가입 API
    @PostMapping("/users")
    public ResponseEntity<ApiResponse<UserSignupResponse>> signup(@Valid @RequestBody UserSignupRequest request) {
        UserSignupResponse response = userService.signup(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of("USER_CREATED", response));
    }


    //회원 정보 조회
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<GetUserResponse>> getUser(
            @PathVariable Integer userId,
            @AuthenticationPrincipal Integer loginUserId
    ) {
        GetUserResponse response = userService.getUserInfo(userId, loginUserId);
        return ResponseEntity.ok(ApiResponse.of("USER_INFO", response));
    }

    //회원 정보 수정
    @PatchMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UpdateUserResponse>> updateUser(
            @PathVariable Integer userId,
            @AuthenticationPrincipal Integer loginUserId,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        UpdateUserResponse response = userService.updateUserInfo(userId, loginUserId, request);
        return ResponseEntity.ok(ApiResponse.of("USER_MODIFIED", response));
    }

    //비밀번호 변경
    @PutMapping("/users/{userId}/password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @PathVariable Integer userId,
            @AuthenticationPrincipal Integer loginUserId,
            @Valid @RequestBody UpdatePasswordRequest request
    ){
        userService.updatePassword(userId, loginUserId, request);
        return ResponseEntity.noContent().build();
    }

    //회원 탈퇴
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable Integer userId,
            @AuthenticationPrincipal Integer loginUserId
    ){
        userService.deleteById(userId, loginUserId);
        return ResponseEntity.noContent().build();
    }
}
