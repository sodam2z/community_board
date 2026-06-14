package community_board.repository;

import community_board.domain.ProfileImage;
import community_board.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileImageRepository extends JpaRepository<ProfileImage, Integer> {
    Optional<ProfileImage> findByUser(User user);
    List<ProfileImage> findByIsActiveFalse();

    // 배치에서 탈퇴 유저와 연결된 프로필 이미지 조회
    @Query(value = "SELECT * FROM profile_image WHERE user_id IN (:userIds)", nativeQuery = true)
    List<ProfileImage> findAllByUserIdsForCleanup(@Param("userIds") List<Integer> userIds);
}
