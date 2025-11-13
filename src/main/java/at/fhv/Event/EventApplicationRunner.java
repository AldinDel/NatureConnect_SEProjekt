package at.fhv.Event;

import at.fhv.Event.domain.model.event.Event;
import at.fhv.Event.persistence.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventApplicationRunner implements ApplicationRunner {

    @Autowired
    private EventRepository eventRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Beispiel: kleine Test-Ausgabe oder Seed-Daten
        List<Event> events = eventRepository.findAll();
        events.forEach(event ->
                System.out.println("Event: " + event.getTitle() + " — " + event.getDescription())
        );
    }
}
