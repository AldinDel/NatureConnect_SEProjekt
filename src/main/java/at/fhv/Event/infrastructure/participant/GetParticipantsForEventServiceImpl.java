package at.fhv.Event.infrastructure.participant;

import at.fhv.Event.application.event.GetParticipantsForEventService;
import at.fhv.Event.domain.model.booking.*;
import at.fhv.Event.presentation.rest.response.booking.EventParticipantsStats;
import at.fhv.Event.presentation.rest.response.booking.ParticipantDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetParticipantsForEventServiceImpl implements GetParticipantsForEventService {

    private final BookingRepository bookingRepository;
    private final BookingParticipantRepository bookingParticipantRepository;

    public GetParticipantsForEventServiceImpl(BookingRepository bookingRepository,
                                              BookingParticipantRepository bookingParticipantRepository) {
        this.bookingRepository = bookingRepository;
        this.bookingParticipantRepository = bookingParticipantRepository;
    }

    @Override
    public List<ParticipantDTO> getParticipants(Long eventId) {

        List<Booking> bookings = bookingRepository.findByEventId(eventId);

        return bookings.stream()
                .flatMap(b -> bookingParticipantRepository.findByBookingId(b.getId()).stream()
                        .map(p -> new ParticipantDTO(
                                p.getId(),                      // participantId
                                b.getId(),                      // bookingId
                                b.getBookerFullName(),          // booker name
                                p.getFullName(),                // participant name
                                p.getAge(),                     // age
                                b.getStatus().name(),           // booking status
                                b.getPaymentStatus().name(),    // payment status
                                p.getCheckInStatus()            // check-in status (enum)
                        ))
                )
                .collect(Collectors.toList());
    }

    public EventParticipantsStats getStatsForEvent(Long eventId) {

        List<Booking> bookings = bookingRepository.findByEventId(eventId);

        List<BookingParticipant> participants = bookings.stream()
                .flatMap(b -> bookingParticipantRepository.findByBookingId(b.getId()).stream())
                .toList();

        long total = participants.size();
        long arrived = participants.stream()
                .filter(p -> p.getCheckInStatus() == ParticipantStatus.CHECKED_IN)
                .count();

        long notArrived = participants.stream()
                .filter(p -> p.getCheckInStatus() == ParticipantStatus.NOT_ARRIVED)
                .count();


        long registered = participants.stream()
                .filter(p -> p.getCheckInStatus() == ParticipantStatus.REGISTERED)
                .count();


        boolean billingReady = bookings.stream()
                .anyMatch(Booking::isBillingReady);



        return new EventParticipantsStats(total, arrived, notArrived, registered, billingReady);
    }

}
