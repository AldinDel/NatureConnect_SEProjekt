package at.fhv.Event.infrastructure.persistence.participant;

import at.fhv.Event.application.event.GetParticipantsForEventService;
import at.fhv.Event.domain.model.booking.*;
import at.fhv.Event.domain.model.event.Event;
import at.fhv.Event.domain.model.event.EventRepository;
import at.fhv.Event.domain.model.payment.PaymentMethod;
import at.fhv.Event.domain.model.payment.PaymentStatus;
import at.fhv.Event.infrastructure.persistence.feedback.FeedbackRepository;
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
    private final EventRepository eventRepository;
    private final FeedbackRepository feedbackRepository;



    public GetParticipantsForEventServiceImpl(
            BookingRepository bookingRepository,
            BookingParticipantRepository bookingParticipantRepository,
            EventRepository eventRepository,
            FeedbackRepository feedbackRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.bookingParticipantRepository = bookingParticipantRepository;
        this.eventRepository = eventRepository;
        this.feedbackRepository = feedbackRepository;
    }


    private boolean isActiveBooking(Booking booking) {
        return booking.getStatus() == BookingStatus.CONFIRMED;
    }

    @Override
    public List<ParticipantDTO> getParticipantsForCheckIn(Long eventId) {

        List<Booking> bookings = bookingRepository.findByEventId(eventId).stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                .filter(b ->
                        b.getPaymentStatus() == PaymentStatus.PAID ||
                                b.getPaymentMethod() == PaymentMethod.ON_SITE ||
                                b.getPaymentMethod() == PaymentMethod.INVOICE
                )

                .toList();

        return bookings.stream()
                .flatMap(b ->
                        bookingParticipantRepository.findByBookingId(b.getId()).stream()
                                .filter(p -> p.getCheckOutStatus() == ParticipantCheckOutStatus.NOT_CHECKED_OUT)
                                .map(p -> {
                                    boolean checkedOut =
                                            p.getCheckOutStatus() == ParticipantCheckOutStatus.CHECKED_OUT;

                                    boolean feedbackExists =
                                            feedbackRepository.existsByBookingParticipantId(p.getId());

                                    return new ParticipantDTO(
                                            p.getId(),
                                            b.getId(),
                                            b.getBookerFullName(),
                                            p.getFullName(),
                                            p.getAge(),
                                            b.getStatus().name(),
                                            (b.getPaymentMethod() == PaymentMethod.ON_SITE)
                                                    ? "PAY ON SITE"
                                                    : (b.getPaymentMethod() == PaymentMethod.INVOICE)
                                                    ? "INVOICE"
                                                    : b.getPaymentStatus().name(),
                                            p.getCheckInStatus(),
                                            checkedOut,
                                            feedbackExists
                                    );
                                })

                )

                .toList();
    }

    @Override
    public List<ParticipantDTO> getParticipants(Long eventId) {

        List<Booking> bookings = bookingRepository.findByEventId(eventId).stream()
                .filter(this::isActiveBooking)
                .toList();

        return bookings.stream()
                .flatMap(b ->
                        bookingParticipantRepository.findByBookingId(b.getId()).stream()
                                .map(p -> {
                                    boolean checkedOut =
                                            p.getCheckOutStatus() == ParticipantCheckOutStatus.CHECKED_OUT;

                                    boolean feedbackExists =
                                            feedbackRepository.existsByBookingParticipantId(p.getId());

                                    return new ParticipantDTO(
                                            p.getId(),
                                            b.getId(),
                                            b.getBookerFullName(),
                                            p.getFullName(),
                                            p.getAge(),
                                            b.getStatus().name(),
                                            (b.getPaymentMethod() == PaymentMethod.ON_SITE)
                                                    ? "PAY ON SITE"
                                                    : (b.getPaymentMethod() == PaymentMethod.INVOICE)
                                                    ? "INVOICE"
                                                    : b.getPaymentStatus().name(),
                                            p.getCheckInStatus(),
                                            checkedOut,
                                            feedbackExists
                                    );
                                })

                )
                .collect(Collectors.toList());
    }

    @Override
    public EventParticipantsStats getStatsForEvent(Long eventId) {

        List<BookingParticipant> participants =
                bookingRepository.findByEventId(eventId).stream()
                        .filter(this::isActiveBooking)
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
                        .filter(this::isActiveBooking)
                        .anyMatch(Booking::isBillingReady);

        return new EventParticipantsStats(
                total,
                arrived,
                notArrived,
                registered,
                billingReady
        );
    }

    @Override
    public EventCheckoutStats getCheckoutStats(Long eventId) {

        List<BookingParticipant> participants =
                bookingRepository.findByEventId(eventId).stream()
                        .filter(this::isActiveBooking)
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

    @Override
    public int getRemainingSpots(Long eventId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        int maxSeats = event.getMaxParticipants();
        int occupiedSeats = bookingRepository.countOccupiedSeatsForEvent(eventId);

        return Math.max(0, maxSeats - occupiedSeats);
    }



}
