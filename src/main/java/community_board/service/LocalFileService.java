package community_board.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class LocalFileService implements FileService {
    @Value("${file.upload-dir}")
    private String uploadDir;

    //파일 업로드 메서드
    @Override
    public String uploadFile(File file){
        try {
            //디렉토리 없으면 생성
            Path directory = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(directory);

            //파일명 충돌 방지
            String fileName = UUID.randomUUID().toString() + "_" + file.getName();
            Path targetPath = directory.resolve(fileName);

            //파일 저장
            Files.copy(file.toPath(), targetPath);

            return targetPath.toString();
    } catch (IOException e){
            throw new RuntimeException("파일 저장 실패",e);
        }
    }

    @Override
    public void deleteFile(String path){
        try {
            Files.deleteIfExists(Paths.get(path));
        } catch (IOException e) {
            throw new RuntimeException("파일 삭제 실패", e);
        }
    }
}
