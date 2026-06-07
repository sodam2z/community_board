package community_board.repository;

import community_board.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByUserId(Integer userId);
    Optional<RefreshToken> findByToken(String token);

    // 특정 사용자 ID에 연결된 모든 리프레시 토큰 일괄 삭제
    void deleteByUserId(Integer userId);
}
