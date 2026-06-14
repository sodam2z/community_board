package community_board.repository;

import community_board.domain.Post;
import community_board.domain.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Integer> {
    List<PostImage> findByPost(Post post);
    List<PostImage> findByIsActiveFalse();
}
