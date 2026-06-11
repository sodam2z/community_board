package community_board.service;

import community_board.domain.Post;
import community_board.domain.PostComment;
import community_board.domain.User;
import community_board.dto.comment.CreateCommentRequest;
import community_board.dto.comment.CreateCommentResponse;
import community_board.dto.comment.UpdateCommentRequest;
import community_board.dto.comment.UpdateCommentResponse;
import community_board.global.exception.*;
import community_board.repository.CommentRepository;
import community_board.repository.PostRepository;
import community_board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor// final 붙거나 @NotNull이 붙은 필드의 생성자 추가
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    //댓글 작성
    @Transactional
    public CreateCommentResponse save(Integer postId, Integer loginUserId, CreateCommentRequest request) {
        //댓글 작성자 조회
        User user = userRepository.findById(loginUserId).orElseThrow(
                () -> new RestApiException(UserErrorCode.USER_NOT_FOUND)
        );

        //댓글 달고자 하는 게시물 조회
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new RestApiException(PostErrorCode.POST_NOT_FOUND)
        );

        //댓글 생성 및 저장
        PostComment savedComment = commentRepository.save(request.toEntity(user,post));
        return CreateCommentResponse.from(savedComment);

    }

    //댓글 수정
    @Transactional
    public UpdateCommentResponse update(Integer commentId, Integer loginUserId, UpdateCommentRequest request) {
        PostComment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new RestApiException(CommentErrorCode.COMMENT_NOT_FOUND)
        );

        // 본인 댓글만 수정 가능
        if (!comment.getUser().getUserId().equals(loginUserId)) {
            throw new RestApiException(CommonErrorCode.FORBIDDEN_ACCESS);
        }

        comment.update(request.getContent());
        return UpdateCommentResponse.from(comment);
    }

    //댓글 삭제
    @Transactional
    public void deleteById(Integer commentId, Integer loginUserId) {
        PostComment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new RestApiException(CommentErrorCode.COMMENT_NOT_FOUND)
        );

        //본인 댓글만 삭제 가능
        if (!comment.getUser().getUserId().equals(loginUserId)) {
            throw new RestApiException(CommonErrorCode.FORBIDDEN_ACCESS);
        }

        comment.delete();
    }
}
