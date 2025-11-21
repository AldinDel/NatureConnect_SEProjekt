package at.fhv.Event.infrastructure.persistence.payment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoucherJpaRepository extends JpaRepository<VoucherEntity, Long> {
    Optional<VoucherEntity> findByCodeIgnoreCase(String code);
}
