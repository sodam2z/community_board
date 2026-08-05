package community_board.controller;

import community_board.config.TokenCookieFactory;
import community_board.dto.CreateAccessTokenRequest;
import community_board.dto.CreateAccessTokenResponse;
import community_board.global.exception.RestApiException;
import community_board.global.exception.UserErrorCode;
import community_board.global.response.ApiResponse;
import community_board.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TokenController {
    private static final int ACCESS_TOKEN_EXPIRATION = 15 * 60;

    private final TokenService tokenService;
    private final TokenCookieFactory tokenCookieFactory;

    @PostMapping("/token")
    public ResponseEntity<ApiResponse<CreateAccessTokenResponse>> createAccessToken(
            @Valid @RequestBody(required = false) CreateAccessTokenRequest request,
            @CookieValue(value = "refreshToken", required = false) String refreshTokenCookie,
            //재발급 쿠키도 요청이 HTTPS였는지 보고 Secure 속성을 정한다.
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {
        String refreshToken = request != null && StringUtils.hasText(request.getRefreshToken())
                ? request.getRefreshToken()
                : refreshTokenCookie;

        if (!StringUtils.hasText(refreshToken)) {
            throw new RestApiException(UserErrorCode.INVALID_REFRESH_TOKEN);
        }

        String newAccessToken = tokenService.createNewAccessToken(refreshToken);
        addAccessTokenCookie(httpRequest, httpResponse, newAccessToken);

        CreateAccessTokenResponse response = new CreateAccessTokenResponse(newAccessToken);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of("TOKEN_CREATED", response));
    }

    private void addAccessTokenCookie(
            HttpServletRequest request,
            HttpServletResponse response,
            String accessToken
    ) {
        tokenCookieFactory.addTokenCookie(request, response, "accessToken", accessToken, ACCESS_TOKEN_EXPIRATION);
    }
}
