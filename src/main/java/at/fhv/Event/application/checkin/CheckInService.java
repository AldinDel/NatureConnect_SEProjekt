package at.fhv.Event.application.checkin;

import at.fhv.Event.domain.model.booking.*;
import at.fhv.Event.domain.model.equipment.Equipment;
import at.fhv.Event.domain.model.equipment.EquipmentRepository;
import at.fhv.Event.domain.model.event.Event;
import at.fhv.Event.domain.model.event.EventRepository;
import at.fhv.Event.domain.model.exception.EventNotFoundException;
import at.fhv.Event.domain.model.payment.PaymentMethod;
import at.fhv.Event.domain.model.payment.PaymentStatus;
import at.fhv.Event.infrastructure.persistence.booking.BookingEntity;
import at.fhv.Event.presentation.rest.request.WalkInBookingRequest;
import at.fhv.Event.presentation.rest.request.WalkInEquipmentRequest;
import at.fhv.Event.presentation.rest.request.WalkInParticipantRequest;
import at.fhv.Event.presentation.rest.response.booking.BookingEquipmentDTO;
import at.fhv.Event.domain.model.booking.BookingParticipant;
import at.fhv.Event.domain.model.booking.BookingParticipantRepository;
import at.fhv.Event.domain.model.booking.ParticipantCheckInStatus;
import at.fhv.Event.domain.model.exception.ParticipantNotCheckedInException;
import at.fhv.Event.domain.model.exception.ParticipantNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CheckInService {

    private final BookingRepository bookingRepository;
    private final BookingParticipantRepository participantRepository;
    private final EventRepository eventRepository;
    private final BookingEquipmentRepository bookingEquipmentRepository;
    private final EquipmentRepository equipmentRepository;


    public CheckInService(
            BookingRepository bookingRepository,
            BookingParticipantRepository participantRepository,
            EventRepository eventRepository,
            BookingEquipmentRepository bookingEquipmentRepository,
            EquipmentRepository equipmentRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.participantRepository = participantRepository;
        this.eventRepository = eventRepository;
        this.bookingEquipmentRepository = bookingEquipmentRepository;
        this.equipmentRepository = equipmentRepository;
    }


    public void checkIn(Long participantId) {
        BookingParticipant p = participantRepository.findById(participantId)
                .orElseThrow(() -> new RuntimeException("Participant not found"));

        p.setCheckInStatus(ParticipantCheckInStatus.CHECKED_IN);
        participantRepository.save(p);
    }

    public void markNotArrived(Long participantId) {
        BookingParticipant p = participantRepository.findById(participantId)
                .orElseThrow(() -> new ParticipantNotCheckedInException(participantId));

        p.setCheckInStatus(ParticipantCheckInStatus.NOT_ARRIVED);
        participantRepository.save(p);
    }
    public void resetStatus(Long participantId) {
        BookingParticipant p = participantRepository.findById(participantId)
                .orElseThrow(() -> new ParticipantNotFoundException(participantId));

        p.setCheckInStatus(ParticipantCheckInStatus.REGISTERED);
        participantRepository.save(p);
    }
    public void createWalkInBooking(Long eventId, WalkInBookingRequest request) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        Booking booking = new Booking();
        booking.setEventId(eventId);
        booking.setEventDate(event.getDate());

        booking.setBookerFirstName(request.getBookerFirstName());
        booking.setBookerLastName(request.getBookerLastName());
        booking.setBookerEmail(request.getEmail());
        booking.setSeats(request.getParticipants().size());
        booking.setStatus(BookingStatus.CONFIRMED);

        booking.setPaymentMethod(
                PaymentMethod.valueOf(request.getPaymentMethod())
        );
        booking.setPaymentStatus(PaymentStatus.PAID);

        Booking savedBooking = bookingRepository.save(booking);

        BookingEntity bookingEntity =
                bookingRepository.findEntityById(savedBooking.getId());

        if (request.getEquipment() != null && !request.getEquipment().isEmpty()) {

            for (WalkInEquipmentRequest e : request.getEquipment()) {

                Equipment equipment = equipmentRepository.findById(e.getEquipmentId())
                        .orElseThrow(() ->
                                new IllegalArgumentException("Equipment not found: " + e.getEquipmentId())
                        );

                BookingEquipment bookingEquipment = new BookingEquipment(
                        savedBooking.getId(),
                        equipment.getId(),
                        e.getQuantity(),
                        equipment.getUnitPrice()
                );

                bookingEquipmentRepository.save(bookingEquipment, bookingEntity);
            }
        }




        for (WalkInParticipantRequest p : request.getParticipants()) {
            BookingParticipant participant = BookingParticipant.createNew(
                    savedBooking.getId(),
                    p.getFirstName(),
                    p.getLastName(),
                    p.getAge()
            );

            participant.setCheckInStatus(ParticipantCheckInStatus.CHECKED_IN);
            participantRepository.save(participant);
        }
    }
    public boolean bookingHasEquipment(Long bookingId) {
        return !bookingEquipmentRepository.findByBookingId(bookingId).isEmpty();
    }

    public List<BookingEquipmentDTO> getBookingEquipment(Long bookingId) {

        List<BookingEquipment> equipmentList =
                bookingEquipmentRepository.findByBookingId(bookingId);

        return equipmentList.stream()
                .map(be -> {
                    Equipment equipment = equipmentRepository.findById(be.getEquipmentId())
                            .orElseThrow(() ->
                                    new IllegalArgumentException("Equipment not found: " + be.getEquipmentId())
                            );

                    return new BookingEquipmentDTO(
                            be.getId(),
                            equipment.getName(),
                            be.getQuantity()
                    );

                })
                .toList();
    }

    @Transactional
    public void returnEquipmentAndCheckout(Long participantId, Long bookingId) {

        List<BookingEquipment> equipmentList =
                bookingEquipmentRepository.findByBookingId(bookingId);

        for (BookingEquipment be : equipmentList) {

            Equipment equipment = equipmentRepository.findById(be.getEquipmentId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Equipment not found: " + be.getEquipmentId())
                    );


            equipment.setStock(equipment.getStock() + be.getQuantity());
            equipmentRepository.save(equipment);


            bookingEquipmentRepository.deleteById(be.getId());
        }

        BookingParticipant participant = participantRepository.findById(participantId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Participant not found: " + participantId)
                );

        participant.setCheckInStatus(ParticipantCheckInStatus.CHECKED_OUT);
        participantRepository.save(participant);
    }



}
