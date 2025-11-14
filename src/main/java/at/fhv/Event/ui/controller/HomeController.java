package at.fhv.Event.ui.controller;

import at.fhv.Event.application.event.SearchEventService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final SearchEventService searchService;

    public HomeController(SearchEventService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("events", searchService.getAll());
        return "nature_connect";
    }
}
