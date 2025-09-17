package com.example.spliteasybackend.shared.infrastructure.storage;

import org.springframework.web.multipart.MultipartFile;

public interface ObjectStorageService {
    /**
     * Sube el archivo y retorna una URL pública (o segura).
     * @param file archivo a subir
     * @param keyPrefix prefijo/carpeta ej: "receipts/mc-15"
     */
    String upload(MultipartFile file, String keyPrefix);
}
