//토큰 생성 및 검증
package community_board.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtProvider {
    //Base64 문자열을 JWT 서명에 사용할 비밀키로 변환한다.
    private Key key;

    public JwtProvider(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
    }
    //AccessToken 발급
    public String createAccessToken(Integer userId) {
        long accessTtlSec = 15 * 60;
        return Jwts.builder()
                .setSubject(String.valueOf(userId))//어떤 유저인지 식별
                .setIssuedAt(new Date())//발급시각
                .setExpiration(Date.from(Instant.now().plusSeconds(accessTtlSec)))//만료시각
                .signWith(key, SignatureAlgorithm.HS256)//서명
                .compact();//최종적인 signature 형태의 문자열 반환
    }

    //토큰 검증
    public Jws<Claims> parse(String jwt) {
        // 서명이 위조되었거나 만료된 토큰이면 JJWT가 예외를 발생시킨다.
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt);
    }

    //RefreshToken 발급
    public String createRefreshToken(Integer userId) {
        long refreshTtlSec = 14L * 24 * 3600; // 14일
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("typ", "refresh")//Refresh Token 명시
                .setId(UUID.randomUUID().toString())//토큰마다 고유한 ID 부여
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plusSeconds(refreshTtlSec)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
