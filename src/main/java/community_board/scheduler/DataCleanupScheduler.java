package community_board.scheduler;

import community_board.domain.PostImage;
import community_board.domain.ProfileImage;
import community_board.repository.CommentRepository;
import community_board.repository.PostImageRepository;
import community_board.repository.PostLikeRepository;
import community_board.repository.PostRepository;
import community_board.repository.ProfileImageRepository;
import community_board.repository.RefreshTokenRepository;
import community_board.repository.UserRepository;
import community_board.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataCleanupScheduler {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostImageRepository postImageRepository;
    private final ProfileImageRepository profileImageRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final FileService fileService;

    // 매주 월요일 새벽 3시에 실행
    @Scheduled(cron = "0 0 3 * * MON")
    @Transactional
    public void cleanupSoftDeletedData() {
        log.info("소프트 딜리트 데이터 정리 배치 시작");

        // 30일 이전 기준 시간
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);

        // 1.보관 기간이 지난 탈퇴 유저 ID 조회
        List<Integer> deletedUserIds = userRepository.findDeletedIdsBefore(cutoff);

        // 2.보관 기간이 지난 게시글과 탈퇴 유저가 작성한 게시글 ID 조회
        Set<Integer> postIdSet = new LinkedHashSet<>(postRepository.findDeletedIdsBefore(cutoff));
        if (!deletedUserIds.isEmpty()) {
            postIdSet.addAll(postRepository.findIdsByUserIdsForCleanup(deletedUserIds));
        }
        List<Integer> deletedPostIds = List.copyOf(postIdSet);

        // 3.게시글과 유저를 참조하는 좋아요 하드 딜리트
        int deletedLikeCount = 0;
        if (!deletedPostIds.isEmpty()) {
            deletedLikeCount += postLikeRepository.deleteAllByPostIdsForCleanup(deletedPostIds);
        }
        if (!deletedUserIds.isEmpty()) {
            deletedLikeCount += postLikeRepository.deleteAllByUserIdsForCleanup(deletedUserIds);
        }
        log.info("삭제된 좋아요 {}개 하드 딜리트", deletedLikeCount);

        // 4.게시글과 유저를 참조하는 댓글 하드 딜리트
        int deletedCommentCount = 0;
        if (!deletedPostIds.isEmpty()) {
            deletedCommentCount += commentRepository.deleteAllByPostIdsForCleanup(deletedPostIds);
        }
        if (!deletedUserIds.isEmpty()) {
            deletedCommentCount += commentRepository.deleteAllByUserIdsForCleanup(deletedUserIds);
        }

        // 5.개별 삭제 후 보관 기간이 지난 댓글 하드 딜리트
        deletedCommentCount += commentRepository.deleteDeletedBeforeForCleanup(cutoff);
        log.info("삭제된 댓글 {}개 하드 딜리트", deletedCommentCount);

        // 6.게시글 이미지 파일과 DB 데이터 하드 딜리트
        if (!deletedPostIds.isEmpty()) {
            List<PostImage> postImages = postImageRepository.findAllByPostIdsForCleanup(deletedPostIds);
            postImages.forEach(image -> {
                fileService.deleteFile(image.getJpgPath());
                if (image.getWebpPath() != null) fileService.deleteFile(image.getWebpPath());
            });
            postImageRepository.deleteAllInBatch(postImages);
            log.info("삭제된 게시글 이미지 {}개 하드 딜리트", postImages.size());
        }

        // 7.댓글, 좋아요, 이미지 삭제 후 게시글 하드 딜리트
        int deletedPostCount = 0;
        if (!deletedPostIds.isEmpty()) {
            deletedPostCount = postRepository.deleteAllByIdsForCleanup(deletedPostIds);
        }
        log.info("삭제된 게시글 {}개 하드 딜리트", deletedPostCount);

        // 8.탈퇴 유저의 프로필 이미지 파일과 DB 데이터 하드 딜리트
        if (!deletedUserIds.isEmpty()) {
            List<ProfileImage> profileImages = profileImageRepository.findAllByUserIdsForCleanup(deletedUserIds);
            profileImages.forEach(image -> {
                fileService.deleteFile(image.getJpgPath());
                if (image.getWebpPath() != null) fileService.deleteFile(image.getWebpPath());
                if (image.getThumbnailPath() != null) fileService.deleteFile(image.getThumbnailPath());
            });
            profileImageRepository.deleteAllInBatch(profileImages);
            log.info("삭제된 프로필 이미지 {}개 하드 딜리트", profileImages.size());
        }

        // 9.탈퇴 유저의 리프레시 토큰 하드 딜리트
        int deletedRefreshTokenCount = 0;
        if (!deletedUserIds.isEmpty()) {
            deletedRefreshTokenCount = refreshTokenRepository.deleteAllByUserIdsForCleanup(deletedUserIds);
        }
        log.info("삭제된 리프레시 토큰 {}개 하드 딜리트", deletedRefreshTokenCount);

        // 10.모든 연관 데이터 삭제 후 유저 하드 딜리트
        int deletedUserCount = 0;
        if (!deletedUserIds.isEmpty()) {
            deletedUserCount = userRepository.deleteAllByIdsForCleanup(deletedUserIds);
        }
        log.info("삭제된 유저 {}개 하드 딜리트", deletedUserCount);

        log.info("소프트 딜리트 데이터 정리 배치 완료");
    }
}
