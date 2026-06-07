package community_board.service;

import community_board.domain.Post;
import community_board.domain.User;
import community_board.dto.post.*;
import community_board.global.exception.PostErrorCode;
import community_board.global.exception.RestApiException;
import community_board.global.exception.UserErrorCode;
import community_board.repository.PostRepository;
import community_board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor// final 붙거나 @NotNull이 붙은 필드의 생성자 추가
@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    //게시글 추가 메서드
    @Transactional
    public CreatePostResponse save(Integer userId, CreatePostRequest request) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RestApiException(UserErrorCode.USER_NOT_FOUND)
        );
        Post savedPost = postRepository.save(request.toEntity(user));
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

    //게시글 수정
    @Transactional
    public UpdatePostResponse update(Integer postId, UpdatePostRequest request) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new RestApiException(PostErrorCode.POST_NOT_FOUND)
        );
        String newTitle = request.getTitle() != null ? request.getTitle() : post.getTitle();
        String newContent = request.getContent() != null ? request.getContent() : post.getContent();
        post.update(newTitle, newContent);

        return UpdatePostResponse.from(post);
    }

    //게시글 삭제
    @Transactional
    public void deleteById(Integer postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new RestApiException(PostErrorCode.POST_NOT_FOUND)
        );
        postRepository.delete(post);
    }

}
