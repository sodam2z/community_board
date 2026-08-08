package community_board.service;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class TrendingScoreCalculator {
    // 트렌딩 점수 계산에 쓰는 기본 가중치
    // 나중에 실제 데이터 보고 조정하기 쉽게 상수로 분리
    private static final double VIEW_WEIGHT = 1.0;
    private static final double LIKE_WEIGHT = 5.0;
    private static final double COMMENT_WEIGHT = 3.0;
    private static final double GRAVITY = 1.8;

    // 현재 시각 기준으로 트렌딩 점수 계산
    public double calculateTrendingScore(
            int views,
            int likeCount,
            int commentCount,
            LocalDateTime createdAt
    ) {
        return calculateTrendingScore(views, likeCount, commentCount, createdAt, LocalDateTime.now());
    }

    double calculateTrendingScore(
            int views,
            int likeCount,
            int commentCount,
            LocalDateTime createdAt,
            LocalDateTime now
    ) {
        double popularity = (views * VIEW_WEIGHT)
                + (likeCount * LIKE_WEIGHT)
                + (commentCount * COMMENT_WEIGHT);

        // 반응이 하나도 없는 신규 게시글은 음수 점수가 나오지 않도록 0점 처리
        if (popularity <= 0) {
            return 0.0;
        }

        // 작성 후 지난 시간을 시간 단위로 계산
        double elapsedHours = Math.max(0.0, Duration.between(createdAt, now).toSeconds() / 3600.0);
        return (popularity - 1) / Math.pow(elapsedHours + 2, GRAVITY);
    }
}
