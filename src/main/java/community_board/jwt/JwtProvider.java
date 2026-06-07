// JWT를 생성하고 올바른 토큰인지 유효성 검사 및 토큰에서 필요한 정보 가져오는 클래스
package community_board.jwt;

import community_board.config.JwtProperties;
import community_board.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private static final Duration ACCESS_TOKEN_DURATION = Duration.ofMinutes(15);
    private static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);
    private static final String TOKEN_TYPE_CLAIM = "tokenType";
    private static final String ACCESS_TOKEN_TYPE = "access";
    private static final String REFRESH_TOKEN_TYPE = "refresh";

    private final JwtProperties jwtProperties;

    public String generateToken(User user, Duration expiredAt) {
        return generateToken(user, expiredAt, ACCESS_TOKEN_TYPE);
    }

    public String generateAccessToken(User user) {
        return generateToken(user, ACCESS_TOKEN_DURATION, ACCESS_TOKEN_TYPE);
    }

    public String generateRefreshToken(User user) {
        return generateToken(user, REFRESH_TOKEN_DURATION, REFRESH_TOKEN_TYPE);
    }

    private String generateToken(User user, Duration expiredAt, String tokenType) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiredAt.toMillis());

        return makeToken(expiry, user, tokenType);
    }

    // JWT 토큰 생성 메서드
    private String makeToken(Date expiry, User user, String tokenType) {
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .setSubject(user.getEmail())
                .claim("id", user.getUserId())
                .claim(TOKEN_TYPE_CLAIM, tokenType)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // JWT 토큰 유효성 검증 메서드
    public boolean validToken(String token) {
        return validToken(token, null);
    }

    public boolean validAccessToken(String token) {
        return validToken(token, ACCESS_TOKEN_TYPE);
    }

    public boolean validRefreshToken(String token) {
        return validToken(token, REFRESH_TOKEN_TYPE);
    }

    private boolean validToken(String token, String expectedTokenType) {
        if (token == null || token.isBlank()) {
            return false;
        }

        try {
            Claims claims = getClaims(token);
            return expectedTokenType == null
                    || expectedTokenType.equals(claims.get(TOKEN_TYPE_CLAIM, String.class));
        } catch (Exception exception) { // 복호화 과정에서 에러 나면 유효하지 않은 토큰
            return false;
        }
    }

    // 토큰 기반으로 인증 정보를 가져오는 메서드
    public Authentication getAuthentication(String token) {
        Integer userId = getUserId(token);

        // 현재는 role이 없으므로 권한 목록은 비워 둔다.
        return new UsernamePasswordAuthenticationToken(
                userId,
                token,
                Collections.emptyList()
        );
    }

    // 토큰 기반으로 유저 ID 가져오는 메서드
    public Integer getUserId(String token) {
        return getClaims(token).get("id", Integer.class);
    }

    private Claims getClaims(String token) {
        return getParser()
                .parseClaimsJws(token)
                .getBody();
    }

    private JwtParser getParser() {
        return Jwts.parserBuilder()
                .requireIssuer(jwtProperties.getIssuer())
                .setSigningKey(getSigningKey())
                .build();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecretKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
