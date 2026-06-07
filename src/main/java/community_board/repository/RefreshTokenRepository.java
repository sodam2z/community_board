package community_board.repository;

import community_board.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    //메서드 이름만으로 쿼리를 생성, 주어진 토큰이면서 아직 무효화되지 않은 레코드 Optional로 조회
    Optional<RefreshToken> findByTokenAndRevokedFalse(String token);

    //특정 사용자 ID에 연결된 모든 리프레시 토큰 일괄 삭제
    void deleteByUserId(Integer userId);
}
