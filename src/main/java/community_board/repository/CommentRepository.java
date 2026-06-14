package community_board.repository;

import community_board.domain.PostComment;
import community_board.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
