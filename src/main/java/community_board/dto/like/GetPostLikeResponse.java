package community_board.dto.like;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class GetPostLikeResponse {
    private Integer likeCount;
    private Boolean isLiked;

    public static GetPostLikeResponse of(Integer likeCount, boolean isLiked) {
        return new GetPostLikeResponse(likeCount, isLiked);
    }
}
