package at.fhv.Event.application.booking;


import at.fhv.Event.application.request.booking.VoucherRequest;
import at.fhv.Event.infrastructure.persistence.booking.VoucherEntity;
import at.fhv.Event.infrastructure.persistence.booking.VoucherJpaRepository;
import at.fhv.Event.rest.response.booking.VoucherValidationResponseDTO;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class VoucherService {
    private final VoucherJpaRepository _repository;

    public VoucherService(VoucherJpaRepository repository) {
        _repository = repository;
    }

    public Optional<VoucherRequest> findByCode(String code) {
        return _repository.findByCodeIgnoreCase(code.trim())
                .map(this::toDto);
    }

    public VoucherValidationResponseDTO validate(String code) {
        VoucherValidationResponseDTO response = new VoucherValidationResponseDTO();
        if (code == null || code.isBlank()) {
            response.valid = false;
            response.message = "Voucher code is required";
            return response;
        }
        Optional<VoucherEntity> opt = _repository.findByCodeIgnoreCase(code.trim());
        if (opt.isEmpty()) {
            response.valid = false;
            response.message = "Voucher code " + code + " not found";
            return response;
        }

        VoucherEntity v = opt.get();
        if (!v.isActive()) {
            response.valid = false;
            response.message = "Voucher is not active";
            return response;
        }

        response.valid = true;
        response.discountPercent = v.getDiscountPercent();
        response.message = "Voucher valid";
        return response;
    }

    @Transactional
    public VoucherRequest create (String code, Integer percent, LocalDateTime from, LocalDateTime until, Integer maxUsers) {
        VoucherEntity v = new VoucherEntity();
        v.setCode(code.trim().toUpperCase());
        v.setDiscountPercent(percent);
        v.setValidFrom(from);
        v.setValidUntil(until);
        v.setMaxUsage(maxUsers);
        v.setUsedCount(0);
        VoucherEntity saved = _repository.save(v);
        return toDto(saved);
    }

    @Transactional
    public boolean consume(String code) {
        Optional<VoucherEntity> opt = _repository.findByCodeIgnoreCase(code.trim());
        if (opt.isEmpty()) {
            return false;
        }
        VoucherEntity v = opt.get();
        if (!v.isActive()) {
            return false;
        }

        v.setUsedCount(v.getUsedCount() + 1);
        _repository.save(v);
        return true;
    }

    private VoucherRequest toDto(VoucherEntity e) {
        VoucherRequest dto = new VoucherRequest();
        dto.code = e.getCode();
        dto.discountPercent = e.getDiscountPercent();
        dto.validFrom = e.getValidFrom();
        dto.validUntil = e.getValidUntil();
        dto.maxUsage = e.getMaxUsage();
        dto.usedCount = e.getUsedCount();
        dto.active = e.isActive();
        return dto;
    }

}
