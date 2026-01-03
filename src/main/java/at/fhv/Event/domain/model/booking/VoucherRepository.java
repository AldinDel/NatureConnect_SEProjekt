package at.fhv.Event.domain.model.booking;

import java.util.Optional;

public interface VoucherRepository {
    Optional<Voucher> findByCode(String code);
    Voucher save(Voucher voucher);
    boolean existsByCode(String code);
}
