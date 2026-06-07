package community_board.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import community_board.dto.post.PostListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

import static community_board.domain.QPost.post;

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
                .map(PostListResponse::from)
                .toList();

        boolean hasNext = content.size()> pageable.getPageSize();
        if (hasNext) {
            content = content.subList(0, pageable.getPageSize());
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }
}
