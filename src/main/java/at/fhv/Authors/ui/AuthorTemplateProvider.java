package at.fhv.Authors.ui;

import at.fhv.Authors.domain.Author;
import at.fhv.Authors.persistence.AuthorRepository;
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
    private AuthorRepository authorRepository;

    @GetMapping("/authors")
    public ModelAndView getAuthorTemplate(Model model) {
        List<Author> authors = Arrays.asList(new Author("Ralph", "Hoch"), new Author("FH", "Vorarlberg"));
        return new ModelAndView("nature_connect", "authors", authors);
    }

    @GetMapping("/ui/events")
    public String showEventList() {
        // einfach zur "echten" Liste weiterleiten
        return "redirect:/events";
    }
}


