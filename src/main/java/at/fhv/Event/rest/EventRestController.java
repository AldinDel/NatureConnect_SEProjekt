package at.fhv.Event.rest;

import at.fhv.Event.domain.Events;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class EventRestController {

    @GetMapping("/getAuthors")
    public List<Events> getAuthors() {
        List<Events> events = new ArrayList<Events>();
        events.add(new Events("John", "Doe"));
        return events;
    }

    @PostMapping("/createAuthor")
    public Events createAuthor(@RequestBody Events events) {
        // TBD: process events
        System.out.println(events);
        return events;
    }
}
