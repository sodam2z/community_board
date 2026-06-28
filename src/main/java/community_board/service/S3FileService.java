package community_board.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@Primary
public class S3FileService implements FileService {

    private final S3Client s3Client;
    private final String bucket;
    private final String cloudfrontDomain;

    // application.yml의 S3 버킷과 CloudFront 도메인 설정 주입
    public S3FileService(
            S3Client s3Client,
            @Value("${cloud.aws.s3.bucket}") String bucket,
            @Value("${cloudfront.domain}") String cloudfrontDomain
    ) {
        this.s3Client = s3Client;
        this.bucket = bucket;
        this.cloudfrontDomain = removeTrailingSlash(cloudfrontDomain);
    }

    @Override
    public String uploadFileName(File file) {
        // DB 저장용 경로는 S3 object key로 반환
        String key = createKey(file);

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(resolveContentType(file))
                .build();

        s3Client.putObject(request, RequestBody.fromFile(file));

        return key;
    }

    @Override
    public void deleteFile(String path) {
        // 삭제할 key가 없으면 그대로 종료
        if (!StringUtils.hasText(path)) {
            return;
        }

        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(path)
                .build();

        s3Client.deleteObject(request);
    }

    @Override
    public Resource loadFile(String path) {
        // 기존 파일 다운로드 API와 호환되도록 S3 객체를 Resource로 감싸서 반환
        return new S3ObjectResource(s3Client, bucket, path);
    }

    // S3 key를 브라우저에서 접근할 수 있는 CloudFront URL로 변환
    public String getFileUrl(String key) {
        if (!StringUtils.hasText(key)) {
            return null;
        }
        return cloudfrontDomain + "/" + encodeKey(key);
    }

    // 파일명 충돌 방지를 위해 UUID를 붙여 S3 key 생성
    private String createKey(File file) {
        String filename = StringUtils.cleanPath(file.getName());
        return "images/" + UUID.randomUUID() + "_" + filename;
    }

    // 변환된 이미지 확장자에 맞춰 S3 Content-Type 지정
    private String resolveContentType(File file) {
        String name = file.getName().toLowerCase();
        if (name.endsWith(".webp")) {
            return "image/webp";
        }
        return "image/jpeg";
    }

    // 공백, 한글 등 특수문자가 포함된 key도 URL에서 안전하게 사용
    private String encodeKey(String key) {
        return URLEncoder.encode(key, StandardCharsets.UTF_8)
                .replace("+", "%20")
                .replace("%2F", "/");
    }

    // 설정값에 슬래시가 붙어 있어도 URL이 중복 슬래시로 만들어지지 않도록 처리
    private String removeTrailingSlash(String domain) {
        if (domain.endsWith("/")) {
            return domain.substring(0, domain.length() - 1);
        }
        return domain;
    }

    private static class S3ObjectResource extends AbstractResource {
        private final S3Client s3Client;
        private final String bucket;
        private final String key;

        // S3 객체를 Spring Resource처럼 다루기 위한 어댑터
        private S3ObjectResource(S3Client s3Client, String bucket, String key) {
            this.s3Client = s3Client;
            this.bucket = bucket;
            this.key = key;
        }

        @Override
        public String getDescription() {
            return "S3 object s3://" + bucket + "/" + key;
        }

        @Override
        public String getFilename() {
            return key;
        }

        @Override
        public boolean exists() {
            // 파일 존재 여부는 S3 HeadObject로 확인
            try {
                s3Client.headObject(headObjectRequest());
                return true;
            } catch (NoSuchKeyException e) {
                return false;
            } catch (S3Exception e) {
                if (e.statusCode() == 404) {
                    return false;
                }
                throw e;
            }
        }

        @Override
        public long contentLength() {
            return s3Client.headObject(headObjectRequest()).contentLength();
        }

        @Override
        public InputStream getInputStream() throws IOException {
            // 컨트롤러에서 Resource 응답 시 실제 S3 객체 스트림 반환
            try {
                return s3Client.getObject(GetObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build());
            } catch (S3Exception e) {
                throw new IOException("S3 파일 조회 실패", e);
            }
        }

        private HeadObjectRequest headObjectRequest() {
            return HeadObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
        }
    }
}
