package community_board.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AnonymousCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

    // IAM Role, 환경변수 등 기본 인증 체인을 사용해 S3 클라이언트 생성
    @Bean
    @Profile("!training")  // AOT Cache 훈련 실행 시에는 S3 접근이 불가능하므로 이 빈을 생성하지 않음
    public S3Client s3Client(@Value("${cloud.aws.region.static}") String region) {
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    // AOT Cache 훈련 실행 전용 — 실제 S3에 연결하지 않고, S3Client 타입 빈만 존재하게 해서
    // 이를 의존하는 다른 빈들이 정상적으로 생성되도록 함
    @Bean
    @Profile("training")
    public S3Client mockS3Client() {
        return S3Client.builder()
                .region(Region.AP_NORTHEAST_2)
                .credentialsProvider(AnonymousCredentialsProvider.create())
                .build();
    }
}