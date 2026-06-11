package community_board.service;

import community_board.domain.Post;
import community_board.domain.PostLike;
import community_board.domain.User;
import community_board.dto.like.PostLikeResponse;
import community_board.global.exception.LikeErrorCode;
import community_board.global.exception.PostErrorCode;
import community_board.global.exception.RestApiException;
import community_board.global.exception.UserErrorCode;
import community_board.repository.PostLikeRepository;
import community_board.repository.PostRepository;
import community_board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class LikeService {
    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    //좋아요 생성
    @Transactional
    public PostLikeResponse save(Integer postId, Integer loginUserId ) {
        //유저 조회
        User user = userRepository.findById(loginUserId).orElseThrow(
                () -> new RestApiException(UserErrorCode.USER_NOT_FOUND)
        );

        //게시물 조회
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new RestApiException(PostErrorCode.POST_NOT_FOUND)
        );

        // 이미 좋아요 눌렀으면 예외
        if (postLikeRepository.existsByUserIdAndPostId(user, post)) {
            throw new RestApiException(LikeErrorCode.ALREADY_LIKED);
        }

        //좋아요 저장
        postLikeRepository.save(PostLike.builder()
                .userId(user)
                .postId(post)
                .build());

        //좋아요 수 반환
        Integer likeCount = postLikeRepository.countByPostId(post);
        return PostLikeResponse.of(likeCount);
    }

    //좋아요 삭제
    @Transactional
    public PostLikeResponse delete(Integer postId, Integer loginUserId ) {

        //유저 조회
        User user = userRepository.findById(loginUserId).orElseThrow(
                () -> new RestApiException(UserErrorCode.USER_NOT_FOUND)
        );

        //게시글 조회
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new RestApiException(PostErrorCode.POST_NOT_FOUND)
        );

        //좋아요를 누르지 않았으면 예외
        if (!postLikeRepository.existsByUserIdAndPostId(user, post)) {
            throw new RestApiException(LikeErrorCode.LIKE_NOT_FOUND);
        }

        //좋아요 삭제
        postLikeRepository.deleteByUserIdAndPostId(user,post);

        //좋아요 수 반환
        Integer likeCount = postLikeRepository.countByPostId(post);
        return PostLikeResponse.of(likeCount);
    }
}
