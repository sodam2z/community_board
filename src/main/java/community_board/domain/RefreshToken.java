package community_board.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "refresh_token")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // RefreshToken 식별하는 기본키
    @Column(name = "id", updatable = false)
    private Integer id;

    // 이 토큰을 발급받은 User의 user_id 값
    @Column(name = "user_id", nullable = false, unique = true)
    private Integer userId;

    @Column(nullable = false, unique = true, length = 1000)
    private String token;

    public RefreshToken(Integer userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    public RefreshToken update(String newRefreshToken) {
        this.token = newRefreshToken;
        return this;
    }
}
