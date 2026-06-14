package community_board.repository;


import community_board.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer>, PostRepositoryCustom {
    @Query(value = "SELECT * FROM post WHERE deleted_at IS NOT NULL AND deleted_at < :cutoff", nativeQuery = true)
    List<Post> findDeletedBefore(@Param("cutoff") LocalDateTime cutoff);
}
