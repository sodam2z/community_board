package community_board.scheduler;

import community_board.domain.PostImage;
import community_board.domain.ProfileImage;
import community_board.repository.PostImageRepository;
import community_board.repository.ProfileImageRepository;
import community_board.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImageCleanupScheduler {

    private final ProfileImageRepository profileImageRepository;
    private final PostImageRepository postImageRepository;
    private final FileService fileService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    // 매주 월요일 새벽 2시에 실행
    @Scheduled(cron = "0 0 2 * * MON")
    @Transactional
    public void cleanupImages() {
        log.info("이미지 정리 배치 시작");

        // 1.is_active=false인 프로필 이미지 파일 삭제,DB 삭제
        List<ProfileImage> inactiveProfileImages = profileImageRepository.findByIsActiveFalse();
        inactiveProfileImages.forEach(image -> {
            fileService.deleteFile(image.getJpgPath());
            if (image.getWebpPath() != null) fileService.deleteFile(image.getWebpPath());
            if (image.getThumbnailPath() != null) fileService.deleteFile(image.getThumbnailPath());
        });
        profileImageRepository.deleteAll(inactiveProfileImages);
        log.info("비활성 프로필 이미지 {}개 삭제", inactiveProfileImages.size());

        // 2.is_active=false인 게시글 이미지 파일 삭제, DB 삭제
        List<PostImage> inactivePostImages = postImageRepository.findByIsActiveFalse();
        inactivePostImages.forEach(image -> {
            fileService.deleteFile(image.getJpgPath());
            if (image.getWebpPath() != null) fileService.deleteFile(image.getWebpPath());
        });
        postImageRepository.deleteAll(inactivePostImages);
        log.info("비활성 게시글 이미지 {}개 삭제", inactivePostImages.size());

        // 3.DB에 없는 고아 파일 삭제
        cleanupOrphanFiles();

        log.info("이미지 정리 배치 완료");
    }

    private void cleanupOrphanFiles() {
        // DB에 저장된 모든 파일명 수집
        List<String> dbPaths = new ArrayList<>();

        profileImageRepository.findAll().forEach(image -> {
            if (image.getJpgPath() != null) dbPaths.add(image.getJpgPath());
            if (image.getWebpPath() != null) dbPaths.add(image.getWebpPath());
            if (image.getThumbnailPath() != null) dbPaths.add(image.getThumbnailPath());
        });

        postImageRepository.findAll().forEach(image -> {
            if (image.getJpgPath() != null) dbPaths.add(image.getJpgPath());
            if (image.getWebpPath() != null) dbPaths.add(image.getWebpPath());
        });

        // 기존 절대경로와 새 파일명 형식을 모두 파일명으로 변환
        Set<String> dbFileNames = dbPaths.stream()
                .map(path -> new File(path).getName())
                .collect(Collectors.toSet());

        // 로컬 파일명과 비교해서 DB에 없는 파일 삭제
        File uploadDirectory = new File(uploadDir);
        File[] localFiles = uploadDirectory.listFiles();
        if (localFiles != null) {
            for (File file : localFiles) {
                if (!dbFileNames.contains(file.getName())) {
                    file.delete();
                    log.info("고아 파일 삭제: {}", file.getName());
                }
            }
        }
    }
}
