package community_board.dto.like;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PostLikeResponse {
    private Integer likeCount;

    public static PostLikeResponse of(Integer likeCount) {
        return new PostLikeResponse(likeCount);
    }

}
