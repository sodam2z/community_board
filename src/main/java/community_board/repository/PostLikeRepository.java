package community_board.repository;

import community_board.domain.Post;
import community_board.domain.PostLike;
import community_board.domain.PostLikeId;
import community_board.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, PostLikeId> {
    //특정 유저가 특정 게시글에 좋아요를 눌렀는지 확인
    boolean existsByUserIdAndPostId(User userId, Post postId);

    //좋아요 수 세기
    Integer countByPostId(Post post);

    //좋아요 삭제
    void deleteByUserIdAndPostId(User user, Post post);

    // 게시글에 연결된 좋아요 하드 딜리트
    @Modifying
    @Query(value = "DELETE FROM post_like WHERE post_id IN (:postIds)", nativeQuery = true)
    int deleteAllByPostIdsForCleanup(@Param("postIds") List<Integer> postIds);

    // 탈퇴 유저가 누른 좋아요 하드 딜리트
    @Modifying
    @Query(value = "DELETE FROM post_like WHERE user_id IN (:userIds)", nativeQuery = true)
    int deleteAllByUserIdsForCleanup(@Param("userIds") List<Integer> userIds);
}
