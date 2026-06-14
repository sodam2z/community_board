package community_board.service;

import community_board.domain.Post;
import community_board.global.exception.PostErrorCode;
import community_board.global.exception.RestApiException;
import community_board.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostViewCountService {

    private final PostRepository postRepository;

    // 조회수 증가
    // 조회수 증가가 실패하더라도 게시글 조회에 영향은 없음
    @Transactional
    public void increase(Integer postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new RestApiException(PostErrorCode.POST_NOT_FOUND)
        );
        post.increaseViewCount();
    }
}
