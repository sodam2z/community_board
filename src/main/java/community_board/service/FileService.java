package community_board.service;

import java.io.File;
import org.springframework.core.io.Resource;

public interface FileService {
    String uploadFileName(File file);
    void deleteFile(String path);
    Resource loadFile(String path);
}
