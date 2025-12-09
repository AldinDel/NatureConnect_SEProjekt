package at.fhv.Event.application.event;

import at.fhv.Event.domain.model.booking.BookingRepository;
import at.fhv.Event.domain.model.user.UserAccount;
import at.fhv.Event.domain.model.user.UserAccountRepository;
import at.fhv.Event.presentation.rest.response.event.EventOverviewDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EventAccessService {
    private final BookingRepository _bookingRepository;
    private final UserAccountRepository _userAccountRepository;

    public EventAccessService(BookingRepository bookingRepository, UserAccountRepository userAccountRepository) {
        _bookingRepository = bookingRepository;
        _userAccountRepository = userAccountRepository;
    }

    public List<EventOverviewDTO> filterVisibleEvents(List<EventOverviewDTO> events, Authentication auth) {
        LocalDate today = LocalDate.now();

        if (auth == null) {
            return filterForAnonymous(events,today);
        }

        String role = getUserRole(auth);
        String organizerName = null;

        if ("ORGANIZER".equals(role)) {
            organizerName = getOrganizerName(auth);
        }

        List<EventOverviewDTO> visibleEvents = new ArrayList<>();
        for (EventOverviewDTO event : events) {
            boolean isVisible = isEventVisibleForUser(event,role, organizerName, today);
            if (isVisible) {
                visibleEvents.add(event);
            }
        }
        return visibleEvents;
    }

    public int calculateRemainingSpots(Long eventId, int minParticipants, int maxParticipants) {
        int confirmed = _bookingRepository.countPaidSeatsForEvent(eventId);
        int baseSlots = maxParticipants - minParticipants;
        int remaining = baseSlots - confirmed;

        if (remaining < 0) {
            return 0;
        }
        return remaining;
    }

    public boolean isEventExpired(LocalDate eventDate, LocalTime startTime) {
        LocalDateTime eventStart = LocalDateTime.of(eventDate, startTime);
        LocalDateTime now = LocalDateTime.now();

        return eventStart.isBefore(now);
    }

    public String getCurrentUserFullName(Authentication auth) {
        if (auth == null) {
            return null;
        }
        String email = auth.getName();
        Optional<UserAccount> userOpt = _userAccountRepository.findByEmailIgnoreCase(email);
        if (userOpt.isEmpty()) {
            return null;
        }

        UserAccount user = userOpt.get();
        return user.get_firstName() + " " + user.get_lastName();
    }


    public String determineDisplayOrganizer(String organizer) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                return organizer;
            }
        }
        return "NatureConnect Team";
    }

    private List<EventOverviewDTO> filterForAnonymous(List<EventOverviewDTO> events, LocalDate today) {
        List<EventOverviewDTO> result = new ArrayList<>();

        for (EventOverviewDTO event : events) {
            boolean isCancelled = Boolean.TRUE.equals(event.cancelled());
            boolean isExpired = false;

            if (event.date() != null) {
                isExpired = event.date().isBefore(today);
            }

            if (!isCancelled && !isExpired) {
                result.add(event);
            }
        }

        return result;
    }

    private boolean isEventVisibleForUser(EventOverviewDTO event, String role, String organizerName, LocalDate today) {
        boolean isCancelled = Boolean.TRUE.equals(event.cancelled());
        boolean isExpired = false;

        if (event.date() != null) {
            isExpired = event.date().isBefore(today);
        }

        if (!isCancelled && !isExpired) {
            return true;
        }

        if ("ADMIN".equals(role) || "FRONT".equals(role)) {
            return true;
        }

        if ("ORGANIZER".equals(role) && organizerName != null) {
            String eventOrganizer = event.organizer();
            if (eventOrganizer != null && eventOrganizer.equalsIgnoreCase(organizerName)) {
                return true;
            }
        }

        return false;
    }

    private String getUserRole(Authentication auth) {
        for (GrantedAuthority authority : auth.getAuthorities()) {
            String authName = authority.getAuthority();

            if ("ROLE_ADMIN".equals(authName)) {
                return "ADMIN";
            }
            if ("ROLE_FRONT".equals(authName)) {
                return "FRONT";
            }
            if ("ROLE_ORGANIZER".equals(authName)) {
                return "ORGANIZER";
            }
        }

        return "CUSTOMER";
    }

    private String getOrganizerName(Authentication auth) {
        String email = auth.getName();
        Optional<UserAccount> userOpt = _userAccountRepository.findByEmailIgnoreCase(email);

        if (userOpt.isEmpty()) {
            return null;
        }

        UserAccount user = userOpt.get();
        return user.get_firstName() + " " + user.get_lastName();
    }

}
