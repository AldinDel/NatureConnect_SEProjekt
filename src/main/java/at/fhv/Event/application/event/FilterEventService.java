package at.fhv.Event.application.event;

import at.fhv.Event.domain.model.event.EventRepository;
import at.fhv.Event.presentation.rest.response.event.EventOverviewDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class FilterEventService {

    private final EventRepository eventRepository;
    private final EventMapperDTO mapper;
    private final EventAccessService accessService;

    public FilterEventService(EventRepository eventRepository, EventMapperDTO mapper,  EventAccessService accessService) {
        this.eventRepository = eventRepository;
        this.mapper = mapper;
        this.accessService = accessService;
    }
    public List<EventOverviewDTO> filter(String q,
                                         String category,
                                         String location,
                                         String difficulty,
                                         BigDecimal minPrice,
                                         BigDecimal maxPrice,
                                         LocalDate startDate,
                                         LocalDate endDate,
                                         String sort) {

        var events = eventRepository.findAll();

        if (q != null && !q.isBlank()) {
            String qLower = q.toLowerCase();
            events = events.stream()
                    .filter(e ->
                            (e.getTitle() != null && e.getTitle().toLowerCase().contains(qLower))
                                    || (e.getDescription() != null && e.getDescription().toLowerCase().contains(qLower))
                    )
                    .toList();
        }

        if (category != null && !category.isBlank())
            events = events.stream()
                    .filter(e -> category.equalsIgnoreCase(e.getCategory()))
                    .toList();

        if (location != null && !location.isBlank())
            events = events.stream()
                    .filter(e -> e.getLocation() != null
                            && e.getLocation().toLowerCase().contains(location.toLowerCase()))
                    .toList();

        if (difficulty != null && !difficulty.isBlank())
            events = events.stream()
                    .filter(e -> e.getDifficulty() != null
                            && e.getDifficulty().name().equalsIgnoreCase(difficulty))
                    .toList();

        if (minPrice != null)
            events = events.stream()
                    .filter(e -> e.getPrice() != null
                            && e.getPrice().compareTo(minPrice) >= 0)
                    .toList();

        if (maxPrice != null)
            events = events.stream()
                    .filter(e -> e.getPrice() != null
                            && e.getPrice().compareTo(maxPrice) <= 0)
                    .toList();

        if (startDate != null)
            events = events.stream()
                    .filter(e -> e.getDate() != null && !e.getDate().isBefore(startDate))
                    .toList();

        if (endDate != null)
            events = events.stream()
                    .filter(e -> e.getDate() != null && !e.getDate().isAfter(endDate))
                    .toList();

        if (sort != null) {
            switch (sort) {
                case "priceAsc" -> events.sort((a, b) -> compareNullable(a.getPrice(), b.getPrice()));
                case "priceDesc" -> events.sort((a, b) -> compareNullable(b.getPrice(), a.getPrice()));
                case "dateAsc" -> events.sort((a, b) -> compareNullable(a.getDate(), b.getDate()));
                case "dateDesc" -> events.sort((a, b) -> compareNullable(b.getDate(), a.getDate()));
            }
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        List<EventOverviewDTO> result = new ArrayList<>();
        for (var event : events) {
            String displayOrganizer = accessService.determineDisplayOrganizer(
                    event.getOrganizer()
            );

            EventOverviewDTO dto = mapper.toOverviewDTO(event, displayOrganizer);
            result.add(dto);
        }
        return result;
    }

    public List<EventOverviewDTO> filterExactDate(LocalDate date, String sort) {

        var events = eventRepository.findAll().stream()
                .filter(e -> e.getDate() != null && e.getDate().equals(date))
                .toList();

        if (sort != null) {
            switch (sort) {
                case "priceAsc" -> events.sort((a,b) -> compareNullable(a.getPrice(), b.getPrice()));
                case "priceDesc" -> events.sort((a,b) -> compareNullable(b.getPrice(), a.getPrice()));
                case "dateAsc" -> events.sort((a,b) -> compareNullable(a.getDate(), b.getDate()));
                case "dateDesc" -> events.sort((a,b) -> compareNullable(b.getDate(), a.getDate()));
            }
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        List<EventOverviewDTO> result = new ArrayList<>();
        for (var event : events) {
            String displayOrganizer = accessService.determineDisplayOrganizer(event.getOrganizer());
            EventOverviewDTO dto = mapper.toOverviewDTO(event, displayOrganizer);
            result.add(dto);
        }
        return result;
    }

    private static <T extends Comparable<T>> int compareNullable(T a, T b) {
        if (a == null && b == null) return 0;
        if (a == null) return 1;
        if (b == null) return -1;
        return a.compareTo(b);
    }
}
