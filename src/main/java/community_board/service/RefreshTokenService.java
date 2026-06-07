package community_board.service;

import community_board.domain.RefreshToken;
import community_board.global.exception.RestApiException;
import community_board.global.exception.UserErrorCode;
import community_board.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken findByRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() ->
                        new RestApiException(
                                UserErrorCode.INVALID_REFRESH_TOKEN
                        )
                );
    }
}
