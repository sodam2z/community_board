package community_board.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class TokenCookieFactory {
    private static final String SAME_SITE_POLICY = "Lax";

    //JWT 쿠키는 전부 여기서 같은 보안 속성으로 내려준다.
    public void addTokenCookie(
            HttpServletRequest request,
            HttpServletResponse response,
            String name,
            String value,
            int maxAge
    ) {
        ResponseCookie cookie = ResponseCookie.from(name, value == null ? "" : value)
                .httpOnly(true)
                .secure(isSecureCookie(request))
                .sameSite(SAME_SITE_POLICY)
                .path("/")
                .maxAge(maxAge)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private boolean isSecureCookie(HttpServletRequest request) {
        //Ingress가 넘겨준 HTTPS 정보는 forward-header 설정을 거치면 여기 반영된다.
        return request.isSecure();
    }
}
