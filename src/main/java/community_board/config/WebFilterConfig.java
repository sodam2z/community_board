package community_board.config;

import community_board.jwt.JwtProvider;
import jakarta.servlet.Filter;
import community_board.filter.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class WebFilterConfig {
    private final JwtProvider jwtProvider;

    @Bean
    public FilterRegistrationBean<Filter> jwtAuthFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        // 모든 URL에서 JwtAuthFilter가 먼저 실행되도록 서블릿 필터로 등록한다.
        filterRegistrationBean.setFilter(new JwtAuthFilter(jwtProvider));
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setOrder(1);
        return filterRegistrationBean;
    }
}
