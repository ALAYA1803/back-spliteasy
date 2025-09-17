package com.example.spliteasybackend.shared.infrastructure.storage.cloudinary;

import com.example.spliteasybackend.shared.infrastructure.storage.ObjectStorageService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryObjectStorageService implements ObjectStorageService {

    private final Cloudinary cloudinary;

    public CloudinaryObjectStorageService(
            @Value("${CLOUDINARY_CLOUD_NAME}") String cloudName,
            @Value("${CLOUDINARY_API_KEY}") String apiKey,
            @Value("${CLOUDINARY_API_SECRET}") String apiSecret
    ) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }

    @Override
    public String upload(MultipartFile file, String keyPrefix) {
        try {
            String original = StringUtils.cleanPath(
                    file.getOriginalFilename() != null ? file.getOriginalFilename() : "receipt"
            );
            String safePrefix = (keyPrefix == null || keyPrefix.isBlank())
                    ? "receipts"
                    : keyPrefix.replaceAll("^/+", "").replaceAll("/+$", "");

            String publicId = safePrefix + "/" + UUID.randomUUID();

            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", safePrefix,
                            "public_id", publicId,
                            "resource_type", "auto",
                            "overwrite", false
                    )
            );

            Object url = uploadResult.get("secure_url");
            if (url == null) url = uploadResult.get("url");
            if (url == null) throw new IllegalStateException("No se recibió URL de Cloudinary");
            return url.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Error subiendo archivo a Cloudinary", ex);
        }
    }
}
