package at.fhv.Event.infrastructure.persistence.payment;

import at.fhv.Event.domain.model.booking.Voucher;
import at.fhv.Event.domain.model.booking.VoucherRepository;
import at.fhv.Event.infrastructure.mapper.VoucherMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class VoucherRepositoryImpl implements VoucherRepository {
    private final VoucherJpaRepository jpaRepository;
    private final VoucherMapper mapper;

    public VoucherRepositoryImpl(VoucherJpaRepository jpaRepository, VoucherMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Voucher> findByCode(String code) {
        Optional<VoucherEntity> entityOpt =
                jpaRepository.findByCodeIgnoreCase(code.trim());

        if (entityOpt.isPresent()) {
            return Optional.of(mapper.toDomain(entityOpt.get()));
        }
        return Optional.empty();
    }

    @Override
    public Voucher save(Voucher voucher) {
        VoucherEntity entity = mapper.toEntity(voucher);
        VoucherEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public boolean existsByCode(String code) {
        return jpaRepository.findByCodeIgnoreCase(code.trim()).isPresent();
    }
}
