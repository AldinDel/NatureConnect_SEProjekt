package at.fhv.Event.infrastructure.persistence.event;

import at.fhv.Event.application.event.GetEventsForTodayService;
import at.fhv.Event.application.event.EventMapperDTO;
import at.fhv.Event.domain.model.event.Event;
import at.fhv.Event.domain.model.event.EventRepository;
import at.fhv.Event.presentation.rest.response.event.EventOverviewDTO;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class GetEventsForTodayServiceImpl implements GetEventsForTodayService {

    private final EventRepository eventRepository;
    private final EventMapperDTO eventMapperDTO;

    public GetEventsForTodayServiceImpl(EventRepository eventRepository,
                                        EventMapperDTO eventMapperDTO) {
        this.eventRepository = eventRepository;
        this.eventMapperDTO = eventMapperDTO;
    }

    @Override
    public List<EventOverviewDTO> getEventsForToday() {
        LocalDate today = LocalDate.now();

        List<Event> events = eventRepository.findByDate(today);

        return events.stream()
                .map(e -> eventMapperDTO.toOverviewDTO(e, e.getOrganizer()))
                .toList();
    }
}
