package community_board.repository;

import community_board.dto.post.PostListResponse;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {

    Slice<PostListResponse> findPostList(Pageable pagable);
    
}
