package community_board.scheduler;

import community_board.repository.PostStatsRepository;
import community_board.repository.PostStatsRepository.TrendingScoreTarget;
import community_board.service.TrendingScoreCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrendingScoreScheduler {
    private static final int TRENDING_TARGET_DAYS = 7;
    private static final long TRENDING_SCORE_UPDATE_INTERVAL = 300_000;

    private final PostStatsRepository postStatsRepository;
    private final TrendingScoreCalculator trendingScoreCalculator;

    // 5분마다 최근 게시글 트렌딩 점수 갱신
    @Scheduled(fixedRate = TRENDING_SCORE_UPDATE_INTERVAL)
    public void updateTrendingScores() {
        log.info("트렌딩 점수 갱신 배치 시작");

        LocalDateTime cutoff = LocalDateTime.now().minusDays(TRENDING_TARGET_DAYS);
        List<TrendingScoreTarget> targets;
        try {
            targets = postStatsRepository.findTrendingScoreTargets(cutoff);
        } catch (Exception e) {
            log.warn("트렌딩 점수 갱신 대상 조회 실패", e);
            return;
        }
        log.info("트렌딩 점수 갱신 대상 게시글 {}개 조회", targets.size());

        int successCount = 0;
        int failureCount = 0;

        for (TrendingScoreTarget target : targets) {
            try {
                double score = trendingScoreCalculator.calculateTrendingScore(
                        target.views(),
                        target.likeCount(),
                        target.commentCount(),
                        target.createdAt()
                );

                int updatedRows = postStatsRepository.updateTrendingScore(target.postId(), score);
                if (updatedRows == 0) {
                    throw new IllegalStateException("post_stats row not found");
                }

                successCount++;
            } catch (Exception e) {
                failureCount++;
                log.warn("트렌딩 점수 갱신 실패 - postId: {}", target.postId(), e);
            }
        }

        log.info("트렌딩 점수 갱신 배치 완료 - 성공: {}개, 실패: {}개", successCount, failureCount);
    }
}
