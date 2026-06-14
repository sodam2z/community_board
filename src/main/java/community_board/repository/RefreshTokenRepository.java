package community_board.repository;

import community_board.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByUserId(Integer userId);
    Optional<RefreshToken> findByToken(String token);

    // 특정 사용자 ID에 연결된 모든 리프레시 토큰 일괄 삭제
    void deleteByUserId(Integer userId);

    // 탈퇴 유저의 리프레시 토큰 하드 딜리트
    @Modifying
    @Query(value = "DELETE FROM refresh_token WHERE user_id IN (:userIds)", nativeQuery = true)
    int deleteAllByUserIdsForCleanup(@Param("userIds") List<Integer> userIds);
}
