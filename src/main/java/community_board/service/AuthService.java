package community_board.service;

import community_board.config.TokenCookieFactory;
import community_board.domain.RefreshToken;
import community_board.domain.User;
import community_board.dto.user.UserLoginRequest;
import community_board.dto.user.UserLoginResponse;
import community_board.global.exception.RestApiException;
import community_board.global.exception.UserErrorCode;
import community_board.jwt.JwtProvider;
import community_board.repository.RefreshTokenRepository;
import community_board.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final TokenCookieFactory tokenCookieFactory;

    private static final int ACCESS_TOKEN_EXPIRATION = 15 * 60;
    private static final int REFRESH_TOKEN_EXPIRATION = 14 * 24 * 60 * 60;

    @Transactional
    public UserLoginResponse login(
            UserLoginRequest request,
            //컨트롤러에서 받은 요청 정보를 쿠키 생성 쪽까지 넘겨준다.
            HttpServletRequest httpRequest,
            HttpServletResponse response
    ) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RestApiException(UserErrorCode.LOGIN_FAILED));

        if (!checkPassword(user, request.getPassword())) {
            throw new RestApiException(UserErrorCode.LOGIN_FAILED);
        }

        // 한 사용자에게 Refresh Token을 하나만 허용하기 위해 기존 토큰을 삭제한다.
        refreshTokenRepository.deleteByUserId(user.getUserId());

        refreshTokenRepository.flush();

        // 새 토큰을 만들고 Refresh Token은 DB에 저장한다.
        TokenResponse tokenResponse = generateAndSaveTokens(user);
        // 브라우저가 이후 요청에서 자동 전송하도록 두 토큰을 쿠키에 담는다.
        addTokenCookies(httpRequest, response, tokenResponse);

        return new UserLoginResponse(
                user.getUserId(),
                user.getNickname()
        );
    }

    @Transactional
    public void logout(
            String refreshToken,
            //로그아웃 때 만료시키는 쿠키도 같은 기준으로 만든다.
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            // 로그아웃한 Refresh Token이 다시 사용되지 않도록 DB에서 삭제한다.
            refreshTokenRepository
                    .findByToken(refreshToken)
                    .ifPresent(refreshTokenRepository::delete);
        }
        addTokenCookie(request, response, "accessToken", null, 0);
        addTokenCookie(request, response, "refreshToken", null, 0);
    }

    private TokenResponse generateAndSaveTokens(User user) {
        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user);

        RefreshToken refreshEntity = new RefreshToken(user.getUserId(), refreshToken);
        refreshTokenRepository.save(refreshEntity);

        return new TokenResponse(accessToken, refreshToken);
    }

    private void addTokenCookies(
            HttpServletRequest request,
            HttpServletResponse response,
            TokenResponse tokenResponse
    ) {
        addTokenCookie(request, response, "accessToken", tokenResponse.accessToken(), ACCESS_TOKEN_EXPIRATION);
        addTokenCookie(request, response, "refreshToken", tokenResponse.refreshToken(), REFRESH_TOKEN_EXPIRATION);
    }

    private void addTokenCookie(
            HttpServletRequest request,
            HttpServletResponse response,
            String name,
            String value,
            int maxAge
    ) {
        tokenCookieFactory.addTokenCookie(request, response, name, value, maxAge);
    }

    private boolean checkPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    public record TokenResponse(String accessToken, String refreshToken) {}
}
