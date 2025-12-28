package at.fhv.Event.infrastructure.persistence.participant;

import at.fhv.Event.application.event.GetParticipantsForEventService;
import at.fhv.Event.domain.model.booking.*;
import at.fhv.Event.presentation.rest.response.booking.EventCheckoutStats;
import at.fhv.Event.presentation.rest.response.booking.EventParticipantsStats;
import at.fhv.Event.presentation.rest.response.booking.ParticipantDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetParticipantsForEventServiceImpl implements GetParticipantsForEventService {

    private final BookingRepository bookingRepository;
    private final BookingParticipantRepository bookingParticipantRepository;

    public GetParticipantsForEventServiceImpl(
            BookingRepository bookingRepository,
            BookingParticipantRepository bookingParticipantRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.bookingParticipantRepository = bookingParticipantRepository;
    }

    @Override
    public List<ParticipantDTO> getParticipants(Long eventId) {

        List<Booking> bookings = bookingRepository.findByEventId(eventId);

        return bookings.stream()
                .flatMap(b ->
                        bookingParticipantRepository.findByBookingId(b.getId()).stream()
                                .map(p -> new ParticipantDTO(
                                        p.getId(),
                                        b.getId(),
                                        b.getBookerFullName(),
                                        p.getFullName(),
                                        p.getAge(),
                                        b.getStatus().name(),
                                        b.getPaymentStatus().name(),
                                        p.getCheckInStatus(),
                                        p.getCheckOutStatus() == ParticipantCheckOutStatus.CHECKED_OUT
                                ))
                )
                .collect(Collectors.toList());
    }

    @Override
    public EventParticipantsStats getStatsForEvent(Long eventId) {

        List<BookingParticipant> participants =
                bookingRepository.findByEventId(eventId).stream()
                        .flatMap(b -> bookingParticipantRepository.findByBookingId(b.getId()).stream())
                        .toList();

        long total = participants.size();

        long arrived = participants.stream()
                .filter(p -> p.getCheckInStatus() == ParticipantCheckInStatus.CHECKED_IN)
                .count();

        long notArrived = participants.stream()
                .filter(p -> p.getCheckInStatus() == ParticipantCheckInStatus.NOT_ARRIVED)
                .count();

        long registered = participants.stream()
                .filter(p -> p.getCheckInStatus() == ParticipantCheckInStatus.REGISTERED)
                .count();

        boolean billingReady =
                bookingRepository.findByEventId(eventId).stream()
                        .anyMatch(Booking::isBillingReady);

        return new EventParticipantsStats(
                total,
                arrived,
                notArrived,
                registered,
                billingReady
        );
    }


    public EventCheckoutStats getCheckoutStats(Long eventId) {

        List<BookingParticipant> participants =
                bookingRepository.findByEventId(eventId).stream()
                        .flatMap(b -> bookingParticipantRepository.findByBookingId(b.getId()).stream())
                        .toList();

        long total = participants.size();

        long checkedOut = participants.stream()
                .filter(p -> p.getCheckOutStatus() == ParticipantCheckOutStatus.CHECKED_OUT)
                .count();

        long remaining = total - checkedOut;

        return new EventCheckoutStats(
                total,
                checkedOut,
                remaining
        );
    }


}
