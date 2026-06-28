package community_board.service;

import community_board.domain.Post;
import community_board.domain.PostImage;
import community_board.dto.image.post.PostImageFileResponse;
import community_board.dto.image.post.PostImageResponse;
import community_board.dto.image.post.PostImageUrlResponse;
import community_board.dto.image.profile.PostProfileImageRequest;
import community_board.global.exception.CommonErrorCode;
import community_board.global.exception.ImageErrorCode;
import community_board.global.exception.PostErrorCode;
import community_board.global.exception.RestApiException;
import community_board.repository.PostImageRepository;
import community_board.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostImageService {
    private final FileService fileService;
    private final ImageProcessor imageProcessor;
    private final PostImageRepository postImageRepository;
    private final PostRepository postRepository;
    private final S3FileService s3FileService;

    @Value("${file.max-size}")
    private long maxSize;

    // 게시글 이미지 업로드 - DB 저장 x
    public List<PostImageResponse> uploadPostImages(List<PostProfileImageRequest> requests) {
        if (requests.size() > 2) {
            throw new RestApiException(ImageErrorCode.IMAGE_LIMIT_EXCEEDED);
        }
        return requests.stream()
                .map(this::processAndUpload)
                .toList();
    }

    // 게시글 작성 후 DB 저장
    @Transactional
    public PostImage savePostImage(Integer postId, String jpgPath, String webpPath) {

        // 1.게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RestApiException(PostErrorCode.POST_NOT_FOUND));

        // 2.이미지 장수 제한
        List<PostImage> images = postImageRepository.findByPost(post);
        if (images.size() >= 2) {
            throw new RestApiException(ImageErrorCode.IMAGE_LIMIT_EXCEEDED);
        }

        // 3.이미지 DB 저장
        PostImage postImage = PostImage.create(post, jpgPath);
        postImage.updateWebp(webpPath);

        return postImageRepository.save(postImage);
    }

    // 게시글 이미지 조회
    @Transactional(readOnly = true)
    public List<PostImageUrlResponse> getPostImages(Integer postId) {

        // 1.게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RestApiException(PostErrorCode.POST_NOT_FOUND));

        // 2.이미지 조회
        return postImageRepository.findByPost(post)
                .stream()
                .map(this::createPostImageUrlResponse)
                .toList();
    }

    // 게시글 이미지 실제 파일 조회
    @Transactional(readOnly = true)
    public PostImageFileResponse getPostImageFile(Integer postId, Integer postImageId, String type) {

        // 1.게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RestApiException(PostErrorCode.POST_NOT_FOUND));

        // 2.게시글에 연결된 이미지 조회
        PostImage postImage = postImageRepository.findByPostAndPostImageId(post, postImageId)
                .orElseThrow(() -> new RestApiException(ImageErrorCode.IMAGE_NOT_FOUND));

        // 3.요청한 타입의 파일 조회, webp 파일이 없으면 jpg 파일로 대체
        return switch (type.toLowerCase()) {
            case "jpg" -> getExistingFile(postImage.getJpgPath(), MediaType.IMAGE_JPEG);
            case "webp" -> getWebpOrJpgFile(postImage);
            default -> throw new RestApiException(ImageErrorCode.IMAGE_TYPE_INVALID);
        };
    }

    // 게시글 이미지 수정
    @Transactional
    public List<PostImageUrlResponse> updatePostImages(Integer postId, Integer loginUserId, List<PostProfileImageRequest> requests) {

        // 1.게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RestApiException(PostErrorCode.POST_NOT_FOUND));

        // 2.본인 게시글 확인
        if (!post.getUser().getUserId().equals(loginUserId)) {
            throw new RestApiException(CommonErrorCode.FORBIDDEN_ACCESS);
        }

        // 3.장수 제한 확인
        if (requests.size() > 2) {
            throw new RestApiException(ImageErrorCode.IMAGE_LIMIT_EXCEEDED);
        }

        // 4.기존 이미지 파일 삭제, DB 삭제
        List<PostImage> existingImages = postImageRepository.findByPost(post);
        existingImages.forEach(image -> {
            fileService.deleteFile(image.getJpgPath());
            if (image.getWebpPath() != null) fileService.deleteFile(image.getWebpPath());
        });
        postImageRepository.deleteAll(existingImages);

        // 5.새 이미지 변환, 저장, DB 저장
        return requests.stream()
                .map(request -> {
                    PostImageResponse result = processAndUpload(request);
                    PostImage postImage = PostImage.create(post, result.getJpgPath());
                    postImage.updateWebp(result.getWebpPath());
                    postImageRepository.save(postImage);
                    return createPostImageUrlResponse(postImage);
                })
                .toList();
    }

    // 게시글 삭제 시 이미지 소프트 딜리트 (PostService에서 호출)
    @Transactional
    public void deactivatePostImages(Post post) {
        postImageRepository.findByPost(post)
                .forEach(PostImage::deactivate);
    }

    // PUT에서 파일 없이 요청 시 이미지 즉시 삭제
    @Transactional
    public void deletePostImages(Integer postId, Integer loginUserId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new RestApiException(PostErrorCode.POST_NOT_FOUND)
        );
        if (!post.getUser().getUserId().equals(loginUserId)) {
            throw new RestApiException(CommonErrorCode.FORBIDDEN_ACCESS);
        }
        List<PostImage> images = postImageRepository.findByPost(post);
        images.forEach(image -> {
            fileService.deleteFile(image.getJpgPath());
            if (image.getWebpPath() != null) fileService.deleteFile(image.getWebpPath());
        });
        postImageRepository.deleteAll(images);
    }

    // 검증, 변환, 파일 저장 공통 로직
    private PostImageResponse processAndUpload(PostProfileImageRequest request) {
        // 1.파일 검증
        request.validate(maxSize);

        // 2.이미지 변환
        var processedFiles = imageProcessor.processImage(request.getFile(), "post");

        // 3.변환된 파일 저장, DB 저장용 경로 반환
        String jpgPath = fileService.uploadFileName(processedFiles.getJpgFile());
        String webpPath = fileService.uploadFileName(processedFiles.getWebpFile());

        return PostImageResponse.of(jpgPath, webpPath);
    }

    //게시글 이미지 CloudFront 조회 URL 생성
    private PostImageUrlResponse createPostImageUrlResponse(PostImage postImage) {
        return PostImageUrlResponse.of(
                postImage.getPostImageId(),
                s3FileService.getFileUrl(postImage.getJpgPath()),
                s3FileService.getFileUrl(postImage.getWebpPath())
        );
    }

    //webp 파일이 없으면 jpg 원본 파일 조회
    private PostImageFileResponse getWebpOrJpgFile(PostImage postImage) {
        PostImageFileResponse webpFile = getFileIfExists(
                postImage.getWebpPath(),
                MediaType.parseMediaType("image/webp")
        );

        if (webpFile != null) {
            return webpFile;
        }

        return getExistingFile(postImage.getJpgPath(), MediaType.IMAGE_JPEG);
    }

    //저장된 경로에 실제 파일이 있으면 파일과 형식 반환
    private PostImageFileResponse getFileIfExists(String path, MediaType mediaType) {
        if (path == null) {
            return null;
        }

        Resource resource = fileService.loadFile(path);
        if (!resource.exists() || !resource.isReadable()) {
            return null;
        }

        return PostImageFileResponse.of(resource, mediaType);
    }

    //반드시 존재해야 하는 파일 조회
    private PostImageFileResponse getExistingFile(String path, MediaType mediaType) {
        PostImageFileResponse file = getFileIfExists(path, mediaType);

        if (file == null) {
            throw new RestApiException(ImageErrorCode.IMAGE_NOT_FOUND);
        }

        return file;
    }
}
