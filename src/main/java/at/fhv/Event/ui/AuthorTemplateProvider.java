package at.fhv.Event.ui;

import at.fhv.Event.domain.Events;
import at.fhv.Event.persistence.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.List;

@Controller
public class AuthorTemplateProvider {

    @Autowired
    private BookingRepository bookingRepository;

    @GetMapping("/authors")
    public ModelAndView getAuthorTemplate(Model model) {
        List<Events> events = Arrays.asList(new Events("Ralph", "Hoch"), new Events("FH", "Vorarlberg"));
        return new ModelAndView("nature_connect", "authors", events);
    }

    @GetMapping("/ui/events")
    public String showEventList() {
        // einfach zur "echten" Liste weiterleiten
        return "redirect:/events";
    }
}


