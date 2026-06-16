package community_board.service;

import community_board.domain.ProfileImage;
import community_board.domain.User;
import community_board.dto.image.profile.PostProfileImageRequest;
import community_board.dto.image.profile.ProfileImageFileResponse;
import community_board.dto.image.profile.ProfileImageResponse;
import community_board.dto.image.profile.ProfileImageUrlResponse;
import community_board.global.exception.CommonErrorCode;
import community_board.global.exception.ImageErrorCode;
import community_board.global.exception.RestApiException;
import community_board.global.exception.UserErrorCode;
import community_board.repository.ProfileImageRepository;
import community_board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
    public ProfileImageUrlResponse getProfileImage(Integer userId) {

        //1.유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(UserErrorCode.USER_NOT_FOUND));

        //2.프로필 이미지 조회
        ProfileImage profileImage = profileImageRepository.findByUser(user)
                .orElseThrow(() -> new RestApiException(ImageErrorCode.IMAGE_NOT_FOUND));

        return createProfileImageUrlResponse(userId);
    }

    //프로필 이미지 실제 파일 조회
    @Transactional(readOnly = true)
    public ProfileImageFileResponse getProfileImageFile(Integer userId, String type) {

        //1.유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(UserErrorCode.USER_NOT_FOUND));

        //2.유저의 프로필 이미지 조회
        ProfileImage profileImage = profileImageRepository.findByUser(user)
                .orElseThrow(() -> new RestApiException(ImageErrorCode.IMAGE_NOT_FOUND));

        //3.요청한 타입의 파일 조회, 변환 파일이 없으면 원본 파일로 대체
        return switch (type.toLowerCase()) {
            case "jpg" -> getExistingFile(profileImage.getJpgPath(), MediaType.IMAGE_JPEG);
            case "webp" -> getWebpOrJpgFile(profileImage);
            case "thumbnail" -> getThumbnailOrOriginalFile(profileImage);
            default -> throw new RestApiException(ImageErrorCode.IMAGE_TYPE_INVALID);
        };
    }

    // 프로필 이미지 수정
    @Transactional
    public ProfileImageUrlResponse updateProfileImage(Integer userId, Integer loginUserId, PostProfileImageRequest request) {

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

        return createProfileImageUrlResponse(userId);
    }
    // 검증, 변환, 파일 저장 공통 로직
    private ProfileImageResponse processAndUpload(PostProfileImageRequest request) {
        // 1.파일 검증
        request.validate(maxSize);

        // 2.이미지 변환
        var processedFiles = imageProcessor.processImage(request.getFile(), "profile");

        // 3.변환된 파일 로컬 저장, DB 저장용 파일명 반환
        String jpgPath = fileService.uploadFileName(processedFiles.getJpgFile());
        String webpPath = fileService.uploadFileName(processedFiles.getWebpFile());
        String thumbnailPath = fileService.uploadFileName(processedFiles.getThumbnailFile());

        return ProfileImageResponse.of(jpgPath, webpPath, thumbnailPath);
    }

    //프로필 이미지 타입별 HTTP 조회 URL 생성
    private ProfileImageUrlResponse createProfileImageUrlResponse(Integer userId) {
        String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/users/{userId}/profile-image/file")
                .buildAndExpand(userId)
                .toUriString();

        return ProfileImageUrlResponse.of(
                fileUrl + "?type=jpg",
                fileUrl + "?type=webp",
                fileUrl + "?type=thumbnail"
        );
    }

    //webp 파일이 없으면 jpg 원본 파일 조회
    private ProfileImageFileResponse getWebpOrJpgFile(ProfileImage profileImage) {
        ProfileImageFileResponse webpFile = getFileIfExists(
                profileImage.getWebpPath(),
                MediaType.parseMediaType("image/webp")
        );

        if (webpFile != null) {
            return webpFile;
        }

        return getExistingFile(profileImage.getJpgPath(), MediaType.IMAGE_JPEG);
    }

    //썸네일이 없으면 webp, webp도 없으면 jpg 원본 파일 조회
    private ProfileImageFileResponse getThumbnailOrOriginalFile(ProfileImage profileImage) {
        ProfileImageFileResponse thumbnailFile = getFileIfExists(
                profileImage.getThumbnailPath(),
                MediaType.IMAGE_JPEG
        );

        if (thumbnailFile != null) {
            return thumbnailFile;
        }

        return getWebpOrJpgFile(profileImage);
    }

    //저장된 경로에 실제 파일이 있으면 파일과 형식 반환
    private ProfileImageFileResponse getFileIfExists(String path, MediaType mediaType) {
        if (path == null) {
            return null;
        }

        Resource resource = fileService.loadFile(path);
        if (!resource.exists() || !resource.isReadable()) {
            return null;
        }

        return ProfileImageFileResponse.of(resource, mediaType);
    }

    //반드시 존재해야 하는 파일 조회
    private ProfileImageFileResponse getExistingFile(String path, MediaType mediaType) {
        ProfileImageFileResponse file = getFileIfExists(path, mediaType);

        if (file == null) {
            throw new RestApiException(ImageErrorCode.IMAGE_NOT_FOUND);
        }

        return file;
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
