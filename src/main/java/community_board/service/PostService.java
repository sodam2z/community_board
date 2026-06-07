package community_board.service;

import community_board.domain.Post;
import community_board.domain.User;
import community_board.dto.CreatePostRequest;
import community_board.dto.CreatePostResponse;
import community_board.global.exception.RestApiException;
import community_board.global.exception.UserErrorCode;
import community_board.repository.PostRepository;
import community_board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor// final 붙거나 @NotNull이 붙은 필드의 생성자 추가
@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    //블로그 글 추가 메서드
    public CreatePostResponse save(Integer userId, CreatePostRequest request) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RestApiException(UserErrorCode.USER_NOT_FOUND)
        );
        Post savedPost = postRepository.save(request.toEntity(user));
        return CreatePostResponse.from(savedPost);
    }
}
