package at.fhv.Event.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        String cloudinaryUrl = System.getenv("CLOUDINARY_URL");

        if (cloudinaryUrl == null) {
            throw new IllegalStateException("CLOUDINARY_URL environment variable is not set");
        }
        System.out.println("Using CLOUDINARY_URL: " + cloudinaryUrl.replaceAll(":[^@]+@", ":***@"));

        return new Cloudinary(cloudinaryUrl);
    }
}