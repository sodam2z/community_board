//요청 토큰 검사
package community_board.filter;

import community_board.jwt.JwtProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;

    //필터 제외 경로 목록
    private static final String[] EXCLUDED_PATHS = {
            "/auth", "/refresh", "/error"
    };

    //JWT 인증 없이 접근 가능한 요청인지 검사
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        //true이면 이 요청에는 JWT 검사를 적용하지 않는다.
        return Arrays.stream(EXCLUDED_PATHS).anyMatch(path::startsWith);
    }

    //실제 필터링 로직
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain
    ) throws IOException, ServletException {

        Optional<String> token = extractToken(request);

        //토큰 없음 -> 401 에러
        if (token.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }


        //토큰 검증 및 속성 설정
        if (!validateAndSetAttributes(token.get(), request)) {
            //토큰이 잘못된 경우 -> 401 에러
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        //인증에 성공한 요청만 다음 필터 또는 컨트롤러로 전달한다.
        chain.doFilter(request, response);
    }

    //요청에서 Access Token 추출
    private Optional<String> extractToken(HttpServletRequest request) {
        //Authorization 헤더 먼저 확인한 후, 없으면 쿠키를 확인
        return extractTokenFromHeader(request)
                .or(() -> extractTokenFromCookie(request));
    }


    private Optional<String> extractTokenFromHeader(HttpServletRequest request) {
        //Authorization: Bearer {token} 형식에서 실제 토큰 부분만 꺼낸다.
        return Optional.ofNullable(request.getHeader("Authorization"))
                .filter(header -> header.startsWith("Bearer "))
                .map(header -> header.substring(7));
    }

    private Optional<String> extractTokenFromCookie(HttpServletRequest request) {
        //헤더가 없다면 브라우저가 보낸 accessToken 쿠키를 사용한다.
        return Optional.ofNullable(request.getCookies())
                .stream()
                .flatMap(Arrays::stream)
                .filter(cookie -> "accessToken".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }


    //Access Token 검증 후 인증된 사용자 번호를 요청에 저장
    private boolean validateAndSetAttributes(String token, HttpServletRequest request) {
        try {
            //서명과 만료시간 검증, JWT 내용을 꺼낸다.
            var jws = jwtProvider.parse(token);
            Claims body = jws.getBody();
            // 컨트롤러가 로그인 사용자를 알 수 있도록 요청에 userId를 저장한다.
            request.setAttribute("userId", Integer.valueOf(body.getSubject()));
            return true;
        } catch (Exception exception) {
            return false;
        }
    }
}
