package community_board.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import community_board.dto.post.PostListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

import static community_board.domain.QPost.post;
import static community_board.domain.QPostComment.postComment;
import static community_board.domain.QPostLike.postLike;

@RequiredArgsConstructor
public class PostRepositoryCustomImpl implements PostRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<PostListResponse> findPostList(Pageable pageable){
        List<PostListResponse> content = queryFactory
                .selectFrom(post)
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()+1) // 다음 페이지 존재 여부 확인
                .fetch()
                .stream()
                .map(post -> PostListResponse.from(
                        post,
                        countComments(post),
                        countLikes(post)
                ))
                .toList();

        boolean hasNext = content.size()> pageable.getPageSize();
        if (hasNext) {
            content = content.subList(0, pageable.getPageSize());
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    private Long countComments(community_board.domain.Post postEntity) {
        Long count = queryFactory
                .select(postComment.count())
                .from(postComment)
                .where(postComment.post.eq(postEntity))
                .fetchOne();

        return count == null ? 0L : count;
    }

    private Long countLikes(community_board.domain.Post postEntity) {
        Long count = queryFactory
                .select(postLike.count())
                .from(postLike)
                .where(postLike.postId.eq(postEntity))
                .fetchOne();

        return count == null ? 0L : count;
    }
}
