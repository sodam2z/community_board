package community_board.scheduler;

import community_board.domain.Post;
import community_board.domain.PostComment;
import community_board.domain.User;
import community_board.repository.CommentRepository;
import community_board.repository.PostRepository;
import community_board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataCleanupScheduler {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    // 매주 월요일 새벽 3시에 실행
    @Scheduled(cron = "0 0 3 * * MON")
    @Transactional
    public void cleanupSoftDeletedData() {
        log.info("소프트 딜리트 데이터 정리 배치 시작");

        // 30일 이전 기준 시간
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);

        // 1.댓글 하드 딜리트
        // 게시글보다 먼저 삭제해야 FK 제약 조건 위반이 발생하지 않음
        List<PostComment> deletedComments = commentRepository.findDeletedBefore(cutoff);
        commentRepository.deleteAll(deletedComments);
        log.info("삭제된 댓글 {}개 하드 딜리트", deletedComments.size());

        // 2.게시글 하드 딜리트
        // 유저보다 먼저 삭제해야 FK 제약 조건 위반이 발생하지 않음
        List<Post> deletedPosts = postRepository.findDeletedBefore(cutoff);
        postRepository.deleteAll(deletedPosts);
        log.info("삭제된 게시글 {}개 하드 딜리트", deletedPosts.size());

        // 3.유저 하드 딜리트
        // 댓글, 게시글 삭제 후 마지막에 삭제
        List<User> deletedUsers = userRepository.findDeletedBefore(cutoff);
        userRepository.deleteAll(deletedUsers);
        log.info("삭제된 유저 {}개 하드 딜리트", deletedUsers.size());

        log.info("소프트 딜리트 데이터 정리 배치 완료");
    }
}
