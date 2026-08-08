package community_board.service;

import community_board.domain.Post;
import community_board.domain.PostLike;
import community_board.domain.User;
import community_board.dto.like.GetPostLikeResponse;
import community_board.dto.like.PostLikeResponse;
import community_board.global.exception.CommonErrorCode;
import community_board.global.exception.LikeErrorCode;
import community_board.global.exception.PostErrorCode;
import community_board.global.exception.RestApiException;
import community_board.global.exception.UserErrorCode;
import community_board.repository.PostLikeRepository;
import community_board.repository.PostRepository;
import community_board.repository.PostStatsRepository;
import community_board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class LikeService {
    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final PostStatsRepository postStatsRepository;
    private final UserRepository userRepository;

    //좋아요 조회
    @Transactional(readOnly = true)
    public GetPostLikeResponse findByPostId(Integer postId, Integer loginUserId) {
        //유저 조회
        User user = userRepository.findById(loginUserId).orElseThrow(
                () -> new RestApiException(UserErrorCode.USER_NOT_FOUND)
        );

        //게시글 조회
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new RestApiException(PostErrorCode.POST_NOT_FOUND)
        );

        //좋아요 수와 로그인 유저의 좋아요 여부 반환
        Integer likeCount = postLikeRepository.countByPostId(post);
        boolean isLiked = postLikeRepository.existsByUserIdAndPostId(user, post);
        return GetPostLikeResponse.of(likeCount, isLiked);
    }

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
        validatePostStatsUpdated(postStatsRepository.increaseLikeCount(postId));

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
        validatePostStatsUpdated(postStatsRepository.decreaseLikeCount(postId));

        //좋아요 수 반환
        Integer likeCount = postLikeRepository.countByPostId(post);
        return PostLikeResponse.of(likeCount);
    }

    private void validatePostStatsUpdated(int updatedRows) {
        if (updatedRows == 0) {
            throw new RestApiException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
