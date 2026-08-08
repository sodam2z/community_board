package community_board.config;

import community_board.filter.TokenAuthenticationFilter;
import community_board.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            UrlBasedCorsConfigurationSource corsConfigurationSource
    ) throws Exception {
        return http
                //프론트 서버에서 쿠키를 포함한 API 요청 허용
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                //세션을 안 쓰기 때문에 SPA용 쿠키 기반 CSRF 토큰을 사용한다.
                .csrf(csrf -> csrf.spa())
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(restAuthenticationEntryPoint))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/users", "/auth", "/token", "/images/profile").permitAll()
                        .requestMatchers(HttpMethod.GET, "/users/*/profile-image", "/users/*/profile-image/file").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/auth").permitAll()
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(
                        new TokenAuthenticationFilter(jwtProvider),
                        UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        //로컬 프론트 개발 서버 주소 허용
        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "https://community-board.p-e.kr"
        ));

        //프론트에서 사용할 HTTP 메서드 허용
        configuration.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));

        //프론트가 CSRF 토큰을 X-XSRF-TOKEN 헤더에 담아서 보낼 수 있게 허용한다.
        configuration.setAllowedHeaders(List.of(
                "Authorization", "Content-Type", "X-XSRF-TOKEN"
        ));

        //액세스 토큰과 리프레시 토큰 쿠키 전송 허용
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        //모든 API 요청에 CORS 설정 적용
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
