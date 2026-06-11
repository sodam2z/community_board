package community_board.domain;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
public class PostLikeId implements Serializable {
    private Integer userId;
    private Integer postId;

    public PostLikeId(Integer userId, Integer postId) {
        this.userId = userId;
        this.postId = postId;
    }
}