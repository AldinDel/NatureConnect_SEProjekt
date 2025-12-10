package at.fhv.Event.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary(@Value("${CLOUDINARY_URL:}") String cloudinaryUrl) {
        if (cloudinaryUrl == null || cloudinaryUrl.isBlank()) {
            throw new IllegalStateException("CLOUDINARY_URL is not set");
        }

        System.out.println("Using CLOUDINARY_URL: " + cloudinaryUrl.replaceAll(":[^@]+@", ":***@"));

        return new Cloudinary(cloudinaryUrl);
    }
}
