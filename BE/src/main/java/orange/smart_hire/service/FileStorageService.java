package orange.smart_hire.service;

import orange.smart_hire.exception.FileStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.upload.base-url}")
    private String baseUrl;

    public String storeResume(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileStorageException("Resume file is required");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            throw new FileStorageException("Resume must be a PDF file");
        }

        long maxSizeBytes = 5 * 1024 * 1024;
        if (file.getSize() > maxSizeBytes) {
            throw new FileStorageException("Resume file must be smaller than 5MB");
        }

        try {
            Path uploadPath = Paths.get(uploadDir);
            Files.createDirectories(uploadPath);

            String fileName = UUID.randomUUID() + ".pdf";
            Path targetPath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return baseUrl + "/" + fileName;
        } catch (IOException e) {
            throw new FileStorageException("Failed to store resume file", e);
        }
    }
}