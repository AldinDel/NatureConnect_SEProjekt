package at.fhv.Event.application.booking;


import at.fhv.Event.domain.model.booking.Voucher;
import at.fhv.Event.domain.model.booking.VoucherRepository;
import at.fhv.Event.presentation.rest.response.booking.VoucherValidationResponseDTO;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class VoucherService {
    private final VoucherRepository voucherRepository;

    public VoucherService(VoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
    }

    public Optional<Voucher> findByCode(String code) {
        if (code == null || code.isBlank()) {
            return Optional.empty();
        }

        return voucherRepository.findByCode(code.trim());
    }

    public VoucherValidationResponseDTO validate(String code) {
        VoucherValidationResponseDTO response = new VoucherValidationResponseDTO();

        if (code == null || code.isBlank()) {
            response.valid = false;
            response.message = "Voucher code is required";
            return response;
        }

        Optional<Voucher> voucherOpt = voucherRepository.findByCode(code.trim());

        if (voucherOpt.isEmpty()) {
            response.valid = false;
            response.message = "Voucher code " + code + " not found";
            return response;
        }

        Voucher voucher = voucherOpt.get();
        if (!voucher.isActive()) {
            response.valid = false;
            response.message = "Voucher is not active";
            return response;
        }

        response.valid = true;
        response.discountPercent = voucher.getDiscountPercent();
        response.message = "Voucher valid";

        return response;
    }

    @Transactional
    public Voucher create(String code, Integer percent, LocalDateTime from, LocalDateTime until, Integer maxUsers) {
        Voucher voucher = new Voucher();
        voucher.setCode(code.trim().toUpperCase());
        voucher.setDiscountPercent(percent);
        voucher.setValidFrom(from);
        voucher.setValidUntil(until);
        voucher.setMaxUsage(maxUsers);
        voucher.setUsedCount(0);

        return voucherRepository.save(voucher);
    }

    @Transactional
    public boolean consume(String code) {
        if (code == null || code.isBlank()) {
            return false;
        }

        Optional<Voucher> voucherOpt = voucherRepository.findByCode(code.trim());

        if (voucherOpt.isEmpty()) {
            return false;
        }

        Voucher voucher = voucherOpt.get();

        try {
            voucher.use();
            voucherRepository.save(voucher);
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

}
