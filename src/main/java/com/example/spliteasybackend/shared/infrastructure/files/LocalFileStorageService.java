package com.example.spliteasybackend.shared.infrastructure.files;

import com.example.spliteasybackend.shared.domain.services.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class LocalFileStorageService implements FileStorageService {

    @Value("${app.upload-dir:./uploads}")
    private String uploadDir;

    @Override
    public String store(MultipartFile file) {
        try {
            Files.createDirectories(Path.of(uploadDir));
            String original = file.getOriginalFilename() != null ? file.getOriginalFilename() : "receipt";
            original = StringUtils.cleanPath(original);
            String ext = original.contains(".") ? original.substring(original.lastIndexOf(".")) : "";
            String name = UUID.randomUUID() + ext;
            Path target = Path.of(uploadDir, name);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + name;
        } catch (IOException e) {
            throw new RuntimeException("No se pudo almacenar el archivo", e);
        }
    }
}
