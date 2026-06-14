package community_board.repository;

import community_board.domain.PostComment;
import community_board.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<PostComment, Integer> {
    List<PostComment> findByUser(User user);

    @Query(value = "SELECT * FROM post_comment WHERE deleted_at IS NOT NULL AND deleted_at < :cutoff", nativeQuery = true)
    List<PostComment> findDeletedBefore(@Param("cutoff") LocalDateTime cutoff);

    // 보관 기간이 지난 소프트 딜리트 댓글 하드 딜리트
    @Modifying
    @Query(value = "DELETE FROM post_comment WHERE deleted_at IS NOT NULL AND deleted_at < :cutoff", nativeQuery = true)
    int deleteDeletedBeforeForCleanup(@Param("cutoff") LocalDateTime cutoff);

    // 게시글을 참조하는 모든 댓글 하드 딜리트
    @Modifying
    @Query(value = "DELETE FROM post_comment WHERE post_id IN (:postIds)", nativeQuery = true)
    int deleteAllByPostIdsForCleanup(@Param("postIds") List<Integer> postIds);

    // 탈퇴 유저가 작성한 모든 댓글 하드 딜리트
    @Modifying
    @Query(value = "DELETE FROM post_comment WHERE user_id IN (:userIds)", nativeQuery = true)
    int deleteAllByUserIdsForCleanup(@Param("userIds") List<Integer> userIds);
}
