package at.fhv.Event.application.event;

import at.fhv.Event.domain.model.event.EventRepository;
import at.fhv.Event.rest.response.event.EventDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilterEventService {

    private final EventRepository eventRepository;
    private final at.fhv.Event.application.event.EventMapperDTO mapper;

    public FilterEventService(EventRepository eventRepository, at.fhv.Event.application.event.EventMapperDTO mapper) {
        this.eventRepository = eventRepository;
        this.mapper = mapper;
    }

    public List<EventDTO> filter(String category,
                                 String location,
                                 String difficulty,
                                 BigDecimal minPrice,
                                 BigDecimal maxPrice,
                                 LocalDate startDate,
                                 LocalDate endDate,
                                 String sort) {

        var events = eventRepository.findAll();

        if (category != null && !category.isBlank())
            events = events.stream().filter(e -> category.equalsIgnoreCase(e.getCategory())).collect(Collectors.toList());

        if (location != null && !location.isBlank())
            events = events.stream().filter(e -> e.getLocation() != null && e.getLocation().toLowerCase().contains(location.toLowerCase())).collect(Collectors.toList());

        if (difficulty != null && !difficulty.isBlank())
            events = events.stream().filter(e -> e.getDifficulty() != null && e.getDifficulty().name().equalsIgnoreCase(difficulty)).collect(Collectors.toList());

        if (minPrice != null)
            events = events.stream().filter(e -> e.getPrice() != null && e.getPrice().compareTo(minPrice) >= 0).collect(Collectors.toList());

        if (maxPrice != null)
            events = events.stream().filter(e -> e.getPrice() != null && e.getPrice().compareTo(maxPrice) <= 0).collect(Collectors.toList());

        if (startDate != null)
            events = events.stream().filter(e -> e.getDate() != null && !e.getDate().isBefore(startDate)).collect(Collectors.toList());

        if (endDate != null)
            events = events.stream().filter(e -> e.getDate() != null && !e.getDate().isAfter(endDate)).collect(Collectors.toList());

        // sorting
        if (sort != null) {
            switch (sort) {
                case "priceAsc" -> events.sort((a,b) -> compareNullable(a.getPrice(), b.getPrice()));
                case "priceDesc" -> events.sort((a,b) -> compareNullable(b.getPrice(), a.getPrice()));
                case "dateAsc" -> events.sort((a,b) -> compareNullable(a.getDate(), b.getDate()));
                case "dateDesc" -> events.sort((a,b) -> compareNullable(b.getDate(), a.getDate()));
            }
        }

        return events.stream().map(mapper::toDto).collect(Collectors.toList());
    }

    private static <T extends Comparable<T>> int compareNullable(T a, T b) {
        if (a == null && b == null) return 0;
        if (a == null) return 1;
        if (b == null) return -1;
        return a.compareTo(b);
    }
}
