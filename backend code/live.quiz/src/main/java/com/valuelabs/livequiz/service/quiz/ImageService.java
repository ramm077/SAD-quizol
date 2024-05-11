package com.valuelabs.livequiz.service.quiz;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

@Service
public class ImageService {
    @Value("${image.upload.directory}")
    private String uploadDirectory;

    public String storeImageByBase64URL(byte[] base64URLBytes) {
        String imageName = UUID.randomUUID().toString();
        imageName=imageName.replaceAll("-", "").substring(0, 10);
        String imagePath = uploadDirectory +"/"+ imageName + ".png";
        File folder=new File(uploadDirectory);
        if(!folder.exists()){
            folder.mkdirs();
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(imagePath)) {
            fileOutputStream.write(base64URLBytes);
        } catch (IOException e) {
            System.out.println("Image not saved");
            throw new RuntimeException(e);
        }
        return imagePath;
    }

    public String getImageBase64URL(String blogImagePath) {
        File imageFile = new File(blogImagePath);
        byte[] imageBytes = new byte[(int) imageFile.length()];

        try (FileInputStream fileInputStream = new FileInputStream(imageFile)) {
            fileInputStream.read(imageBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Base64.getEncoder().encodeToString(imageBytes);
    }
}
