package at.fhv.Event.application.user;

import com.cloudinary.Cloudinary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class AvatarService {

    private final Cloudinary cloudinary;

    public AvatarService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String upload(MultipartFile file) {
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
            throw new IllegalStateException("Avatar upload failed", e);
        }
    }

    public void delete(String url) {
        try {
            String publicId = url.substring(url.indexOf("avatars/"))
                    .replace(".jpg", "")
                    .replace(".png", "");

            cloudinary.uploader().destroy(publicId, Map.of());
        } catch (Exception ignored) {}
    }
}
