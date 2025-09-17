package com.example.spliteasybackend.shared.infrastructure.storage;

import com.example.spliteasybackend.shared.domain.services.FileStorageService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Primary
public class FileStorageAdapter implements FileStorageService {

    private final ObjectStorageService objectStorageService;

    public FileStorageAdapter(ObjectStorageService objectStorageService) {
        this.objectStorageService = objectStorageService;
    }

    @Override
    public String store(MultipartFile file) {
        return objectStorageService.upload(file, "receipts");
    }
}
