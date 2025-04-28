package com.syedhamed.ecommerce.service.contract;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String getImageName();
    String getFileExtension(String originalFilename);
    String saveFile( MultipartFile file);
    boolean deleteFile(String fileName);
}
