package at.fhv.Event.application.user;

import at.fhv.Event.domain.model.exception.AvatarUploadException;
import com.cloudinary.Cloudinary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class AvatarService {

    private final Cloudinary cloudinary;

    public AvatarService(@org.springframework.beans.factory.annotation.Autowired(required = false) Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String upload(MultipartFile file) {
        if (cloudinary == null) {
            return null;
        }
        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    Map.of(
                            "folder", "avatars",
                            "resource_type", "image"
                    )
            );
            return result.get("secure_url").toString();
        } catch (Exception e) {
            throw new AvatarUploadException("Avatar upload failed");
        }
    }

    public void delete(String url) {
        if (cloudinary == null) {
            return;
        }
        try {
            String publicId = url.substring(url.indexOf("avatars/"))
                    .replace(".jpg", "")
                    .replace(".png", "");

            cloudinary.uploader().destroy(publicId, Map.of());
        } catch (Exception e) {
            throw new AvatarUploadException("Avatar delete failed");
        }
    }
}
