package at.fhv.Event.application.event;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadImage(MultipartFile file) {
        System.out.println("=== CloudinaryService.uploadImage START ===");

        if (file == null) {
            System.out.println("File is NULL");
            return null;
        }

        if (file.isEmpty()) {
            System.out.println("File is empty (file.isEmpty == true)");
            System.out.println("Filename: " + file.getOriginalFilename());
            System.out.println("Size: " + file.getSize());
            return null;
        }

        System.out.println("Filename: " + file.getOriginalFilename());
        System.out.println("Size: " + file.getSize());

        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("folder", "events")
            );

            System.out.println("UploadResult: " + uploadResult);
            System.out.println("=== CloudinaryService.uploadImage END ===");

            return (String) uploadResult.get("secure_url");
        } catch (Exception e) {
            System.out.println("Error during Cloudinary upload:");
            e.printStackTrace();
            return null;
        }
    }



}
