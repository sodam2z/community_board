package community_board.service;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrendingScoreCalculatorTest {
    private final TrendingScoreCalculator calculator = new TrendingScoreCalculator();

    @Test
    void recentPostWithSmallStatsCanRankHigherThanOldPostWithLargeStats() {
        LocalDateTime now = LocalDateTime.of(2026, 8, 8, 12, 0);

        double oldPostScore = calculator.calculateTrendingScore(
                100_000,
                10_000,
                5_000,
                now.minusYears(1),
                now
        );
        double recentPostScore = calculator.calculateTrendingScore(
                2,
                0,
                0,
                now,
                now
        );

        assertTrue(recentPostScore > oldPostScore);
    }

    @Test
    void zeroStatsAreCalculatedWithoutException() {
        LocalDateTime now = LocalDateTime.of(2026, 8, 8, 12, 0);

        assertDoesNotThrow(() -> calculator.calculateTrendingScore(0, 0, 0, now, now));
    }
}
