package com.example.leave.service;

import com.example.leave.exception.ApiException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {
  private final Path uploadDir;

  public FileStorageService(@Value("${app.files.uploadDir:uploads}") String uploadDir) {
    this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
    try {
      Files.createDirectories(this.uploadDir);
    } catch (IOException ex) {
      throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not create upload directory");
    }
  }

  public String store(MultipartFile file) {
    if (file.isEmpty()) {
      throw new ApiException(HttpStatus.BAD_REQUEST, "File is empty");
    }

    String originalName = file.getOriginalFilename();
    String fileName = UUID.randomUUID() + "_" + (originalName == null ? "file" : originalName);
    Path targetLocation = uploadDir.resolve(fileName);

    try {
      Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
      return fileName;
    } catch (IOException ex) {
      throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not store file");
    }
  }

  public Resource loadAsResource(String fileName) {
    try {
      Path filePath = uploadDir.resolve(fileName).normalize();
      Resource resource = new UrlResource(filePath.toUri());
      if (resource.exists() && resource.isReadable()) {
        return resource;
      }
      throw new ApiException(HttpStatus.NOT_FOUND, "File not found");
    } catch (MalformedURLException ex) {
      throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid file path");
    }
  }
}
