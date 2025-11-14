package at.fhv.Event;

import at.fhv.Event.application.event.SearchEventService;
import at.fhv.Event.rest.response.event.EventOverviewDTO;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventApplicationRunner implements ApplicationRunner {

    private final SearchEventService searchEventService;

    public EventApplicationRunner(SearchEventService searchEventService) {
        this.searchEventService = searchEventService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        List<EventOverviewDTO> events = searchEventService.getAll();

        events.forEach(event ->
                System.out.println("Event: " + event.title() + " — " + event.description())
        );
    }
}
