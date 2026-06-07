package community_board.service;

import community_board.domain.User;
import community_board.global.exception.RestApiException;
import community_board.global.exception.UserErrorCode;
import community_board.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    //전달받은 리프레시 토큰으로 토큰 유효성 검증
    public String createNewAccessToken(String refreshToken) {
        // JWT 서명 및 만료 시간 검증
        if (!jwtProvider.validRefreshToken(refreshToken)) {
            throw new RestApiException(
                    UserErrorCode.INVALID_REFRESH_TOKEN
            );
        }

        Integer userId = refreshTokenService.findByRefreshToken(refreshToken).getUserId();

        User user = userService.findById(userId);

        //새로운 액세스 토큰 생성
        return jwtProvider.generateAccessToken(user);
    }
}
