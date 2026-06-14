package community_board.repository;

import community_board.domain.Post;
import community_board.domain.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Integer> {
    List<PostImage> findByPost(Post post);
    Optional<PostImage> findByPostAndPostImageId(Post post, Integer postImageId);
    List<PostImage> findByIsActiveFalse();

    // 배치에서 게시글과 연결된 이미지 조회
    @Query(value = "SELECT * FROM post_image WHERE post_id IN (:postIds)", nativeQuery = true)
    List<PostImage> findAllByPostIdsForCleanup(@Param("postIds") List<Integer> postIds);
}
