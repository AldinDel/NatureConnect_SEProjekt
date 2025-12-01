package at.fhv.Event.application.booking;

import at.fhv.Event.domain.model.booking.Booking;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class BookingPermissionService {

    private final BookEventService bookEventService;

    public BookingPermissionService(BookEventService bookEventService) {
        this.bookEventService = bookEventService;
    }

    public boolean canEdit(Authentication auth, Long bookingId) {
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return true;
        }

        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CUSTOMER"))) {
            Booking booking = bookEventService.getById(bookingId);

            return booking.getBookerEmail() != null
                    && booking.getBookerEmail().equalsIgnoreCase(auth.getName());
        }

        return false;
    }
}
