package community_board.service;

import community_board.domain.Post;
import community_board.domain.PostImage;
import community_board.dto.image.post.PostImageResponse;
import community_board.dto.image.profile.PostProfileImageRequest;
import community_board.global.exception.ImageErrorCode;
import community_board.global.exception.PostErrorCode;
import community_board.global.exception.RestApiException;
import community_board.repository.PostImageRepository;
import community_board.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    // 검증, 변환, 파일 저장 공통 로직
    private PostImageResponse processAndUpload(PostProfileImageRequest request) {
        request.validate(maxSize);

        var processedFiles = imageProcessor.processImage(request.getFile(), "post");

        String jpgPath = fileService.uploadFile(processedFiles.getJpgFile());
        String webpPath = fileService.uploadFile(processedFiles.getWebpFile());

        return PostImageResponse.of(jpgPath, webpPath);
    }
}