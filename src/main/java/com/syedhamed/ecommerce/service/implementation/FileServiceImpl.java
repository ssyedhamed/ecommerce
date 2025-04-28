package com.syedhamed.ecommerce.service.implementation;

import com.syedhamed.ecommerce.service.contract.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Value("${default.image.product}")
    private String DEFAULT_PRODUCT_IMAGE;
    @Value("${default.image.dir}")
    private String DEFAULT_IMAGE_DIR;

    public String getImageName(){
        return DEFAULT_PRODUCT_IMAGE;
    }


    //this method returns the file extension (.png/.jpeg etc) or empty if the file is without extension (README)
    public String getFileExtension(String originalFilename) {
        int dotIndex = originalFilename.lastIndexOf(".");
        return ((dotIndex == -1) ? "" : originalFilename.substring(dotIndex));
    }

    public String saveFile( MultipartFile file)  {
            // File has data, implement the logic else return empty string
        if(file == null || file.isEmpty()){
            return null;
        }
        try {

                // create dir
                Path dirPath = Paths.get(DEFAULT_IMAGE_DIR);
                Files.createDirectories(dirPath); // throws IOException
                //generate uniqueName for originalFileName
                String fileExtension = getFileExtension(file.getOriginalFilename());
                String uniqueFilename = UUID.randomUUID() + fileExtension;
                // generate full file path (directory + filename)
//                Path fullFilePath = Paths.get(UPLOAD_DIR + uniqueFilename); <- this is not the best practise
            // Safely resolve the full file path by combining the base directory and unique filename, ensuring correct path formatting.
            Path fullFilePath = Paths.get(DEFAULT_IMAGE_DIR).resolve(uniqueFilename);
                //copy the binary data of file (we got from param) to the full file path we generated
                Files.copy(file.getInputStream(), fullFilePath,StandardCopyOption.REPLACE_EXISTING);
//                return the filename
                return uniqueFilename;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean deleteFile(String fileName){
        Path filePath = Paths.get(DEFAULT_IMAGE_DIR).resolve(fileName);
        try {
            Boolean isDeleted = Files.deleteIfExists(filePath);
            return isDeleted;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
