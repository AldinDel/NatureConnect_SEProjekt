package at.fhv.Event.application.booking;

import at.fhv.Event.domain.model.booking.*;
import at.fhv.Event.domain.model.payment.PaymentMethod;
import at.fhv.Event.domain.model.payment.PaymentStatus;
import at.fhv.Event.domain.model.payment.PaymentTransaction;
import at.fhv.Event.domain.model.payment.PaymentTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProcessPaymentService {

    private final BookingRepository bookingRepository;
    private final PaymentTransactionRepository transactionRepo;
    public ProcessPaymentService(
            BookingRepository bookingRepository,
            PaymentTransactionRepository transactionRepo
    ) {
        this.bookingRepository = bookingRepository;
        this.transactionRepo = transactionRepo;
    }

    @Transactional
    public PaymentTransaction processPayment(Long bookingId, PaymentMethod method) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        double amount = booking.getTotalPrice();
        PaymentTransaction tx = new PaymentTransaction(bookingId, method, amount);

        switch (method) {
            case INVOICE -> {
                tx.markSuccess();
                tx.setTransactionReference("INV-" + System.currentTimeMillis());
                booking.setPaymentStatus(PaymentStatus.PAID);
            }
            case ON_SITE -> {
                tx.markSuccess();
                tx.setTransactionReference("ONSITE-" + System.currentTimeMillis());
                booking.setPaymentStatus(PaymentStatus.PAID);
            }
            case CREDIT_CARD -> {
                tx.markSuccess();
                tx.setTransactionReference("CC-" + System.currentTimeMillis());
                booking.setPaymentStatus(PaymentStatus.PAID);
            }
            case PAYPAL -> {
                tx.markSuccess();
                tx.setTransactionReference("PP-" + System.currentTimeMillis());
                booking.setPaymentStatus(PaymentStatus.PAID);
            }
        }

        booking.setStatus(BookingStatus.CONFIRMED);

        PaymentTransaction savedTx = transactionRepo.save(tx);
        bookingRepository.save(booking);

        return savedTx;
    }
}