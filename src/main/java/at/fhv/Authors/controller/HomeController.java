package at.fhv.Authors.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        // Thymeleaf sucht nach templates/index.html
        return "nature_connect";
    }
}
