package community_board.service;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
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

    //DB 저장용 파일명 반환 메서드
    @Override
    public String uploadFileName(File file) {
        return saveFile(file).getFileName().toString();
    }

    //실제 파일 저장 공통 로직
    private Path saveFile(File file) {
        try {
            //1.디렉토리 없으면 생성
            Path directory = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(directory);

            //2.파일명 충돌 방지
            String fileName = UUID.randomUUID().toString() + "_" + file.getName();
            Path targetPath = directory.resolve(fileName);

            //3.파일 저장
            Files.copy(file.toPath(), targetPath);

            return targetPath;
        } catch (IOException e){
            throw new RuntimeException("파일 저장 실패",e);
        }
    }

    @Override
    public void deleteFile(String path){
        try {
            Files.deleteIfExists(resolvePath(path));
        } catch (IOException e) {
            throw new RuntimeException("파일 삭제 실패", e);
        }
    }

    //저장된 파일명을 브라우저에 응답할 수 있는 Resource로 변환
    @Override
    public Resource loadFile(String path) {
        return new FileSystemResource(resolvePath(path));
    }

    //DB에 저장된 파일명을 실제 저장 경로로 변환
    private Path resolvePath(String path) {
        Path savedPath = Paths.get(path);

        //1.기존 DB에 절대경로로 저장된 파일도 조회할 수 있도록 처리
        if (savedPath.isAbsolute()) {
            return savedPath.normalize();
        }

        //2.파일명을 업로드 디렉토리의 실제 경로로 변환
        Path directory = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path resolvedPath = directory.resolve(savedPath).normalize();

        //3.업로드 디렉토리 밖의 파일 접근 방지
        if (!resolvedPath.startsWith(directory)) {
            throw new IllegalArgumentException("잘못된 파일 경로입니다.");
        }

        return resolvedPath;
    }
}
