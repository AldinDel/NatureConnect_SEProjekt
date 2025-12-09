package at.fhv.Event.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = ObjectUtils.asMap(
                "cloud_name", "dwsnx7jof",
                "api_key", "383453321687114",
                "api_secret", "UsJkNMQm7yxGGLyTkzonoEBYmgM"
        );
        return new Cloudinary(config);
    }
}
