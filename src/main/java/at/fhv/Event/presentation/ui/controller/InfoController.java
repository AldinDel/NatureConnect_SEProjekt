package at.fhv.Event.presentation.ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class InfoController {

    @GetMapping("/about")
    public String about() {
        return "info/about";
    }

    @GetMapping("/contact")
    public String contact() {
        return "info/contact";
    }

    @PostMapping("/contact")
    public String contactSubmit() {
        // no persistence, just show success message
        return "redirect:/contact?sent=true";
    }
}

