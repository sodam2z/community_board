package community_board.repository;


import community_board.domain.Post;
import community_board.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer>, PostRepositoryCustom {
    List<Post> findByUser(User user);

    @Query(value = "SELECT * FROM post WHERE deleted_at IS NOT NULL AND deleted_at < :cutoff", nativeQuery = true)
    List<Post> findDeletedBefore(@Param("cutoff") LocalDateTime cutoff);

    // 배치에서 삭제할 게시글 ID 조회
    @Query(value = "SELECT post_id FROM post WHERE deleted_at IS NOT NULL AND deleted_at < :cutoff", nativeQuery = true)
    List<Integer> findDeletedIdsBefore(@Param("cutoff") LocalDateTime cutoff);

    // 탈퇴 유저가 작성한 게시글 ID 조회
    @Query(value = "SELECT post_id FROM post WHERE user_id IN (:userIds)", nativeQuery = true)
    List<Integer> findIdsByUserIdsForCleanup(@Param("userIds") List<Integer> userIds);

    // 댓글, 좋아요, 이미지 삭제 후 게시글 하드 딜리트
    @Modifying
    @Query(value = "DELETE FROM post WHERE post_id IN (:postIds)", nativeQuery = true)
    int deleteAllByIdsForCleanup(@Param("postIds") List<Integer> postIds);
}
