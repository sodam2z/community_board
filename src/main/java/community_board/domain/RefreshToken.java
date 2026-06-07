package community_board.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name = "refresh_token")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // RefreshToken 레코드 자체를 식별하는 기본키
    private Integer id;

    // 이 토큰을 발급받은 User의 user_id 값
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(unique = true, nullable = false)
    private String token;

    @Column(name = "expires_at", nullable = false)
    // DB에서도 만료 여부를 확인하기 위해 보관하는 만료 시각
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    // true이면 아직 만료 전이어도 사용할 수 없는 토큰이다.
    private boolean revoked = false;
}
