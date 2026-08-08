package community_board.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostStatsRepository {
    private final JdbcTemplate jdbcTemplate;

    // 좋아요 수 증가
    public int increaseLikeCount(Integer postId) {
        return jdbcTemplate.update(
                "UPDATE post_stats SET like_count = like_count + 1 WHERE post_id = ?",
                postId
        );
    }

    // 좋아요 수 하락
    public int decreaseLikeCount(Integer postId) {
        return jdbcTemplate.update(
                """
                UPDATE post_stats
                SET like_count = CASE WHEN like_count > 0 THEN like_count - 1 ELSE 0 END
                WHERE post_id = ?
                """,
                postId
        );
    }

    // 댓글 수 증가
    public int increaseCommentCount(Integer postId) {
        return jdbcTemplate.update(
                "UPDATE post_stats SET comment_count = comment_count + 1 WHERE post_id = ?",
                postId
        );
    }

    // 댓글 수 하락
    public int decreaseCommentCount(Integer postId) {
        return jdbcTemplate.update(
                """
                UPDATE post_stats
                SET comment_count = CASE WHEN comment_count > 0 THEN comment_count - 1 ELSE 0 END
                WHERE post_id = ?
                """,
                postId
        );
    }
}
