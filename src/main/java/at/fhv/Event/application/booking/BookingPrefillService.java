package at.fhv.Event.application.booking;

import at.fhv.Event.application.request.booking.CreateBookingRequest;
import at.fhv.Event.domain.model.booking.AudienceType;
import at.fhv.Event.domain.model.user.CustomerProfileRepository;
import org.springframework.stereotype.Service;
import at.fhv.Event.application.request.booking.ParticipantDTO;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
public class BookingPrefillService {

    private final CustomerProfileRepository customerProfileRepository;

    public BookingPrefillService(CustomerProfileRepository repo) {
        this.customerProfileRepository = repo;
    }

    @Transactional(readOnly = true)
    public CreateBookingRequest prepareCreateRequestForLoggedInUser(String email, Long eventId) {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setEventId(eventId);
        request.setAudience(AudienceType.INDIVIDUAL);

        customerProfileRepository.findByEmail(email)
                .ifPresent(customer -> {
                    request.setBookerFirstName(customer.get_firstName());
                    request.setBookerLastName(customer.get_lastName());
                    request.setBookerEmail(customer.get_email());

                    ParticipantDTO p1 = new ParticipantDTO();
                    p1.setFirstName(customer.get_firstName());
                    p1.setLastName(customer.get_lastName());
                    p1.setAge(null);

                    request.setParticipants(List.of(p1));
                    request.setSeats(1);
                });

        return request;
    }
}
