package community_board.service;

import community_board.domain.ProfileImage;
import community_board.domain.User;
import community_board.dto.image.profile.PostProfileImageRequest;
import community_board.dto.image.profile.ProfileImageResponse;
import community_board.global.exception.CommonErrorCode;
import community_board.global.exception.ImageErrorCode;
import community_board.global.exception.RestApiException;
import community_board.global.exception.UserErrorCode;
import community_board.repository.ProfileImageRepository;
import community_board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileImageService {

    private final FileService fileService;
    private final ImageProcessor imageProcessor;
    private final ProfileImageRepository profileImageRepository;
    private final UserRepository userRepository;

    @Value("${file.max-size}")
    private long maxSize;

    // 회원가입 시 이미지 업로드
    public ProfileImageResponse uploadProfileImage(PostProfileImageRequest request) {
        return processAndUpload(request);
    }

    // 회원가입 완료 후 DB에 프로필 이미지 저장
    @Transactional
    public ProfileImage saveProfileImage(Integer userId, String jpgPath, String webpPath, String thumbnailPath) {

        // 1.유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(UserErrorCode.USER_NOT_FOUND));

        // 2.프로필 이미지 DB 저장
        ProfileImage profileImage = ProfileImage.create(user, jpgPath);
        profileImage.updateWebpAndThumbnail(webpPath, thumbnailPath);

        return profileImageRepository.save(profileImage);
    }

    //프로필 이미지 조회
    @Transactional(readOnly = true)
    public ProfileImageResponse getProfileImage(Integer userId, Integer loginUserId) {

        //1.본인 확인
        if (!userId.equals(loginUserId)) {
            throw new RestApiException(CommonErrorCode.FORBIDDEN_ACCESS);
        }

        //2.유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(UserErrorCode.USER_NOT_FOUND));

        //3.프로필 이미지 조회
        ProfileImage profileImage = profileImageRepository.findByUser(user)
                .orElseThrow(() -> new RestApiException(ImageErrorCode.IMAGE_NOT_FOUND));

        return ProfileImageResponse.from(profileImage);
    }

    // 프로필 이미지 수정
    @Transactional
    public ProfileImageResponse updateProfileImage(Integer userId, Integer loginUserId, PostProfileImageRequest request) {

        // 1.본인 확인
        if (!userId.equals(loginUserId)) {
            throw new RestApiException(CommonErrorCode.FORBIDDEN_ACCESS);
        }

        // 2.유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(UserErrorCode.USER_NOT_FOUND));

        // 3.검증, 변환, 파일 저장
        ProfileImageResponse result = processAndUpload(request);

        // 4.기존 프로필 이미지 있으면 업데이트, 없으면 새로 등록
        ProfileImage profileImage = profileImageRepository.findByUser(user)
                .orElse(ProfileImage.create(user, result.getJpgPath()));

        profileImage.update(result.getJpgPath(), result.getWebpPath(), result.getThumbnailPath());
        profileImageRepository.save(profileImage);

        return ProfileImageResponse.from(profileImage);
    }
    // 검증, 변환, 파일 저장 공통 로직
    private ProfileImageResponse processAndUpload(PostProfileImageRequest request) {
        // 1.파일 검증
        request.validate(maxSize);

        // 2.이미지 변환
        var processedFiles = imageProcessor.processImage(request.getFile(), "profile");

        // 3.변환된 파일 로컬 저장
        String jpgPath = fileService.uploadFile(processedFiles.getJpgFile());
        String webpPath = fileService.uploadFile(processedFiles.getWebpFile());
        String thumbnailPath = fileService.uploadFile(processedFiles.getThumbnailFile());

        return ProfileImageResponse.of(jpgPath, webpPath, thumbnailPath);
    }

    // 유저 삭제 시 프로필 이미지 소프트 딜리트
    @Transactional
    public void deactivateProfileImage(User user) {
        profileImageRepository.findByUser(user)
                .ifPresent(ProfileImage::deactivate);
    }

    // 프로필 이미지 삭제 (파일 없이 PUT 요청 시)
    @Transactional
    public void deleteProfileImage(Integer userId, Integer loginUserId) {

        // 1.본인 확인
        if (!userId.equals(loginUserId)) {
            throw new RestApiException(CommonErrorCode.FORBIDDEN_ACCESS);
        }

        // 2.유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(UserErrorCode.USER_NOT_FOUND));

        // 3.기존 프로필 이미지 조회 - 없으면 그냥 넘어감
        profileImageRepository.findByUser(user).ifPresent(profileImage -> {
            fileService.deleteFile(profileImage.getJpgPath());
            if (profileImage.getWebpPath() != null) fileService.deleteFile(profileImage.getWebpPath());
            if (profileImage.getThumbnailPath() != null) fileService.deleteFile(profileImage.getThumbnailPath());
            profileImageRepository.delete(profileImage);
        });
    }
}