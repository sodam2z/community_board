package community_board.service;

import community_board.domain.ProfileImage;
import community_board.domain.User;
import community_board.dto.image.profile.PostProfileImageRequest;
import community_board.dto.image.profile.PostProfileImageResponse;
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
    public PostProfileImageResponse uploadProfileImage(PostProfileImageRequest request) {
        // 1.파일 크기 검증
        if (!request.isFileSizeValid(maxSize)) {
            throw new RestApiException(ImageErrorCode.IMAGE_SIZE_EXCEEDED);
        }

        // 2.확장자 검증
        if (!request.isFileExtensionValid()) {
            throw new RestApiException(ImageErrorCode.IMAGE_INVALID_EXTENSION);
        }

        // 3.이미지 변환
        var processedFiles = imageProcessor.processImage(request.getFile(), "profile");

        // 4.변환된 파일 로컬 저장
        String jpgPath = fileService.uploadFile(processedFiles.getJpgFile());
        String webpPath = fileService.uploadFile(processedFiles.getWebpFile());
        String thumbnailPath = fileService.uploadFile(processedFiles.getThumbnailFile());

        return PostProfileImageResponse.of(jpgPath, webpPath, thumbnailPath);
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
    public PostProfileImageResponse getProfileImage(Integer userId, Integer loginUserId) {

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

        return PostProfileImageResponse.from(profileImage);
    }

}