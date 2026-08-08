package community_board.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostStatsRepository {
    private final JdbcTemplate jdbcTemplate;

    public record TrendingScoreTarget(
            Integer postId,
            int views,
            int likeCount,
            int commentCount,
            LocalDateTime createdAt
    ) {
    }

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

    // 트렌딩 점수 계산에 필요한 게시글 통계 조회
    public List<TrendingScoreTarget> findTrendingScoreTargets(LocalDateTime cutoff) {
        return jdbcTemplate.query(
                """
                SELECT p.post_id,
                       p.views,
                       p.created_at,
                       COALESCE(ps.like_count, 0) AS like_count,
                       COALESCE(ps.comment_count, 0) AS comment_count
                FROM post p
                LEFT JOIN post_stats ps ON ps.post_id = p.post_id
                WHERE p.deleted_at IS NULL
                  AND p.created_at >= ?
                """,
                (rs, rowNum) -> new TrendingScoreTarget(
                        rs.getInt("post_id"),
                        rs.getInt("views"),
                        rs.getInt("like_count"),
                        rs.getInt("comment_count"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ),
                cutoff
        );
    }

    // 계산된 트렌딩 점수 반영
    public int updateTrendingScore(Integer postId, double trendingScore) {
        return jdbcTemplate.update(
                "UPDATE post_stats SET trending_score = ? WHERE post_id = ?",
                trendingScore,
                postId
        );
    }
}
