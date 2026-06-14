//공통으로 사용할 이미지 처리
package community_board.service;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.webp.WebpWriter;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Component
public class ImageProcessor {

    // 전역 DTO
    public static class ProcessedFiles {
        private final File jpgFile;
        private final File webpFile;
        private final File thumbnailFile;

        public ProcessedFiles(File jpgFile, File webpFile, File thumbnailFile) {
            this.jpgFile = jpgFile;
            this.webpFile = webpFile;
            this.thumbnailFile = thumbnailFile;
        }

        public File getJpgFile() { return jpgFile; }
        public File getWebpFile() { return webpFile; }
        public File getThumbnailFile() { return thumbnailFile; }
    }

    // 이미지 처리 메서드
    public ProcessedFiles processImage(MultipartFile file, String type) {
        try {
            // 업로드된 파일을 읽기
            BufferedImage inputImage = ImageIO.read(file.getInputStream());

            // 이미지 타입에 따른 압축률 결정
            float quality = getCompressionQuality(type);

            // JPG 압축
            File jpgFile = compressToJPG(inputImage, quality);

            // WEBP 변환
            File webpFile = convertToWebP(inputImage);

            // 썸네일 생성 - 프로필 이미지만 썸네일용 생성
            File thumbnailFile = type.equals("profile") ? createThumbnail(inputImage, type) : null;

            return new ProcessedFiles(jpgFile, webpFile, thumbnailFile);

        } catch (Exception e) {
            throw new RuntimeException("이미지 변환 실패", e);
        }
    }

    // 이미지 타입에 따른 압축률 반환
    // 프로필 60%, 게시글 원본 20%
    private float getCompressionQuality(String type) {
        return switch (type) {
            case "profile" -> 0.6f;
            case "post" -> 0.2f;
            default -> 0.6f;
        };
    }

    // JPG 압축
    private File compressToJPG(BufferedImage image, float quality) throws IOException {
        File jpgFile = File.createTempFile("jpg_", ".jpg");
        Thumbnails.of(image)
                .scale(1.0)
                .outputQuality(quality)
                .outputFormat("jpg")
                .toFile(jpgFile);
        return jpgFile;
    }

    // WEBP 변환
    private File convertToWebP(BufferedImage image) throws Exception {
        File webpFile = File.createTempFile("webp_", ".webp");
        ImmutableImage.fromAwt(image)
                .output(WebpWriter.DEFAULT, webpFile);
        return webpFile;
    }

    // 썸네일 생성 - 압축률 60%
    private File createThumbnail(BufferedImage image, String type) throws IOException {
        File thumbnailFile = File.createTempFile("thumb_", ".jpg");
        int size = type.equals("profile") ? 150 : 300;
        Thumbnails.of(image)
                .size(size, size)
                .outputQuality(0.6f)
                .outputFormat("jpg")
                .toFile(thumbnailFile);
        return thumbnailFile;
    }
}