package community_board.repository;

import community_board.domain.Post;
import community_board.domain.PostLike;
import community_board.domain.PostLikeId;
import community_board.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, PostLikeId> {
    //특정 유저가 특정 게시글에 좋아요를 눌렀는지 확인
    boolean existsByUserIdAndPostId(User userId, Post postId);

    //좋아요 수 세기
    Integer countByPostId(Post post);
}

