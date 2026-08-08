package community_board.service;

import community_board.domain.Post;
import community_board.domain.User;
import community_board.dto.post.*;
import community_board.event.PostViewedEvent;
import community_board.global.exception.CommonErrorCode;
import community_board.global.exception.PostErrorCode;
import community_board.global.exception.RestApiException;
import community_board.global.exception.UserErrorCode;
import community_board.repository.PostRepository;
import community_board.repository.PostStatsRepository;
import community_board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor// final 붙거나 @NotNull이 붙은 필드의 생성자 추가
@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostImageService postImageService;
    private final PostStatsRepository postStatsRepository;
    private final ApplicationEventPublisher eventPublisher;

    //게시글 추가 메서드
    @Transactional
    public CreatePostResponse save(Integer userId, CreatePostRequest request) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RestApiException(UserErrorCode.USER_NOT_FOUND)
        );
        Post savedPost = postRepository.save(request.toEntity(user));
        validatePostStatsCreated(postStatsRepository.insertInitialStats(savedPost.getPostId()));

        // 이미지 경로가 있으면 DB 저장
        if (request.getJpgPaths() != null) {
            for (int i = 0; i < request.getJpgPaths().size(); i++) {
                postImageService.savePostImage(
                        savedPost.getPostId(),
                        request.getJpgPaths().get(i),
                        request.getWebpPaths().get(i)
                );
            }
        }

        return CreatePostResponse.from(savedPost);
    }

    //게시글 단건 조회
    @Transactional(readOnly = true)
    public GetPostResponse findById(Integer postId) {
        Post post =  postRepository.findById(postId).orElseThrow(
                () -> new RestApiException(PostErrorCode.POST_NOT_FOUND)
        );

        return GetPostResponse.from(post);
    }

    @Transactional(readOnly = true)
    public void increaseViewCount(Integer postId) {
        postRepository.findById(postId).orElseThrow(
                () -> new RestApiException(PostErrorCode.POST_NOT_FOUND)
        );
        //조회수 증가는 GET 조회랑 분리해서 CSRF 검증 대상인 POST에서만 처리한다.
        eventPublisher.publishEvent(new PostViewedEvent(postId));
    }

    //게시글 수정
    @Transactional
    public UpdatePostResponse update(Integer postId, Integer loginUserId, UpdatePostRequest request) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new RestApiException(PostErrorCode.POST_NOT_FOUND)
        );

        //본인 게시물만 수정 가능
        if (!post.getUser().getUserId().equals(loginUserId)) {
            throw new RestApiException(CommonErrorCode.FORBIDDEN_ACCESS);
        }

        String newTitle = request.getTitle() != null ? request.getTitle() : post.getTitle();
        String newContent = request.getContent() != null ? request.getContent() : post.getContent();
        post.update(newTitle, newContent);

        return UpdatePostResponse.from(post);
    }

    //게시글 삭제
    @Transactional
    public void deleteById(Integer postId, Integer loginUserId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new RestApiException(PostErrorCode.POST_NOT_FOUND)
        );

        //본인 게시글만 삭제 가능
        if (!post.getUser().getUserId().equals(loginUserId)) {
            throw new RestApiException(CommonErrorCode.FORBIDDEN_ACCESS);
        }

        // 이미지 소프트 딜리트
        postImageService.deactivatePostImages(post);

        post.delete();
    }

    //게시글 목록 조회
    @Transactional(readOnly = true)
    public Slice<PostListResponse> findPostList(Pageable pageable) {
        return postRepository.findPostList(pageable);
    }

    //트렌딩 게시글 목록 조회
    @Transactional(readOnly = true)
    public Slice<PostListResponse> findTrendingPostList(Pageable pageable) {
        return postRepository.findTrendingPostList(pageable);
    }

    private void validatePostStatsCreated(int updatedRows) {
        if (updatedRows == 0) {
            throw new RestApiException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

}
