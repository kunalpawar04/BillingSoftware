package com.kunal.billingSoftware.service.impl;

import com.kunal.billingSoftware.service.FileUploadService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;

@Service
public class FileUploadServiceImpl implements FileUploadService {

    private final Path uploadPath;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public FileUploadServiceImpl(@Value("${file.upload-dir}") String uploadDir) {
        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    @Override
    public String uploadFile(MultipartFile file) {
        try {
            String filename = generateUniqueFilename(file.getOriginalFilename());
            Path dest = uploadPath.resolve(filename);

            file.transferTo(dest.toFile());

            // Return a URL instead of local path
            return filename;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    @Override
    public boolean deleteFile(String imgUrl) {
        try {
            Path path = uploadPath.resolve(Paths.get(imgUrl).getFileName().toString());
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String generateUniqueFilename(String originalFilename) {
        String ext = "";
        int dotIndex = originalFilename.lastIndexOf(".");
        if (dotIndex >= 0) {
            ext = originalFilename.substring(dotIndex);
        }

        return UUID.randomUUID().toString() + ext;
    }
}
