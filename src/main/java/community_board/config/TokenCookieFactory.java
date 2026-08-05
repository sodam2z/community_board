package community_board.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class TokenCookieFactory {
    private static final String SAME_SITE_POLICY = "Lax";
    private static final String PRODUCTION_ENV = "production";

    private final Environment environment;

    public TokenCookieFactory(Environment environment) {
        this.environment = environment;
    }

    //JWT 쿠키는 전부 여기서 같은 보안 속성으로 내려준다.
    public void addTokenCookie(
            HttpServletResponse response,
            String name,
            String value,
            int maxAge
    ) {
        ResponseCookie cookie = ResponseCookie.from(name, value == null ? "" : value)
                .httpOnly(true)
                .secure(isSecureCookie())
                .sameSite(SAME_SITE_POLICY)
                .path("/")
                .maxAge(maxAge)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private boolean isSecureCookie() {
        //NLB에서 HTTPS가 끝나니까 요청이 아니라 우리가 넘긴 실행 환경값으로 판단한다.
        return PRODUCTION_ENV.equalsIgnoreCase(environment.getProperty("APP_ENV", ""));
    }
}
