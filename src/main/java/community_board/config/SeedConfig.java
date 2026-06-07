package community_board.config;

import community_board.domain.User;
import community_board.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.stream.IntStream;

@Configuration
@Profile("dev")
@RequiredArgsConstructor
public class SeedConfig {
    private final UserRepository userRepository;

    @Bean
    ApplicationRunner seedRunner() {
        return arguments -> seed();
    }

    @Transactional
    void seed() {
        if (userRepository.count() >= 10) return;

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        IntStream.rangeClosed(1, 10).forEach(i -> {
            String rawPassword = "123aS!" + i;
            String encodedPassword = passwordEncoder.encode(rawPassword);
            User user = new User("tester" + i + "@gmail.com", encodedPassword, "tester" + i);
            userRepository.save(user);
        });
    }
}
