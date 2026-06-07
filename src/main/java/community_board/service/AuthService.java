package community_board.service;

import community_board.domain.RefreshToken;
import community_board.domain.User;
import community_board.dto.UserLoginRequest;
import community_board.dto.UserLoginResponse;
import community_board.global.exception.RestApiException;
import community_board.global.exception.UserErrorCode;
import community_board.jwt.JwtProvider;
import community_board.repository.RefreshTokenRepository;
import community_board.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    private static final int ACCESS_TOKEN_EXPIRATION = 15 * 60;
    private static final int REFRESH_TOKEN_EXPIRATION = 14 * 24 * 3600;

    @Transactional
    public UserLoginResponse login(
            UserLoginRequest request,
            HttpServletResponse response
    ) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RestApiException(UserErrorCode.LOGIN_FAILED));

        if (!checkPassword(user, request.getPassword())) {
            throw new RestApiException(UserErrorCode.LOGIN_FAILED);
        }

        // 한 사용자에게 Refresh Token을 하나만 허용하기 위해 기존 토큰을 삭제한다.
        refreshTokenRepository.deleteByUserId(user.getUserId());

        // 새 토큰을 만들고 Refresh Token은 DB에 저장한다.
        TokenResponse tokenResponse = generateAndSaveTokens(user);
        // 브라우저가 이후 요청에서 자동 전송하도록 두 토큰을 쿠키에 담는다.
        addTokenCookies(response, tokenResponse);

        return new UserLoginResponse(
                user.getUserId(),
                user.getNickname()
        );
    }

    @Transactional
    public void logout(String refreshToken, HttpServletResponse response) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            // 로그아웃한 Refresh Token이 다시 사용되지 않도록 DB에서 삭제한다.
            refreshTokenRepository
                    .findByTokenAndRevokedFalse(refreshToken)
                    .ifPresent(refreshTokenRepository::delete);
        }
        addTokenCookie(response, "accessToken", null, 0);
        addTokenCookie(response, "refreshToken", null, 0);
    }

    @Transactional
    public TokenResponse refreshTokens(
            String refreshToken,
            HttpServletResponse response
    ) {
        try {
            // JWT 서명과 JWT 자체의 만료 시간을 먼저 검사한다.
            jwtProvider.parse(refreshToken);

            // DB에도 존재하고 revoked=false인 토큰인지 확인한다.
            RefreshToken entity = refreshTokenRepository
                    .findByTokenAndRevokedFalse(refreshToken)
                    .orElse(null);

            if (entity == null || entity.getExpiresAt().isBefore(LocalDateTime.now())) {
                return null;
            }

            Integer userId = Integer.valueOf(
                    jwtProvider.parse(refreshToken).getBody().getSubject()
            );
            User user = userRepository.findById(userId).orElse(null);

            if (user == null) {
                return null;
            }

            String newAccessToken = jwtProvider.createAccessToken(user.getUserId());
            // Refresh Token은 유지하고 Access Token 쿠키만 교체한다.
            addTokenCookie(response, "accessToken", newAccessToken, ACCESS_TOKEN_EXPIRATION);

            return new TokenResponse(newAccessToken, refreshToken);
        } catch (Exception e) {
            return null;
        }
    }

    private TokenResponse generateAndSaveTokens(User user) {
        String accessToken = jwtProvider.createAccessToken(user.getUserId());
        String refreshToken = jwtProvider.createRefreshToken(user.getUserId());

        RefreshToken refreshEntity = new RefreshToken();
        refreshEntity.setUserId(user.getUserId());
        refreshEntity.setToken(refreshToken);
        refreshEntity.setExpiresAt(LocalDateTime.now().plusSeconds(REFRESH_TOKEN_EXPIRATION));
        refreshEntity.setRevoked(false);
        refreshTokenRepository.save(refreshEntity);

        return new TokenResponse(accessToken, refreshToken);
    }

    private void addTokenCookies(HttpServletResponse response, TokenResponse tokenResponse) {
        addTokenCookie(response, "accessToken", tokenResponse.accessToken(), ACCESS_TOKEN_EXPIRATION);
        addTokenCookie(response, "refreshToken", tokenResponse.refreshToken(), REFRESH_TOKEN_EXPIRATION);
    }

    private void addTokenCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        // JavaScript에서 쿠키를 읽지 못하게 하여 토큰 탈취 위험을 줄인다.
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    private boolean checkPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    public record TokenResponse(String accessToken, String refreshToken) {}
}
