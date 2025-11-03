package at.fhv.Authors.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Hiermit kann dein Browser Bilder aus dem Ordner "uploads" laden
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
