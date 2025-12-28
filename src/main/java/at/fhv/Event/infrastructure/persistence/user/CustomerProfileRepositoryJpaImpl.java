package at.fhv.Event.infrastructure.persistence.user;

import at.fhv.Event.domain.model.user.CustomerProfile;
import at.fhv.Event.domain.model.user.CustomerProfileRepository;
import at.fhv.Event.infrastructure.mapper.CustomerProfileMapper;
import at.fhv.Event.infrastructure.mapper.UserMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CustomerProfileRepositoryJpaImpl implements CustomerProfileRepository {
    private final CustomerProfileJpaRepository _customerProfileJpaRepository;

    public CustomerProfileRepositoryJpaImpl(CustomerProfileJpaRepository customerProfileJpaRepository) {
        _customerProfileJpaRepository = customerProfileJpaRepository;
    }

    @Override
    public Optional<CustomerProfile> findById(Long id) {
        Optional<CustomerProfileEntity> entityOptional = _customerProfileJpaRepository.findById(id);

        if (entityOptional.isEmpty()) {
            return Optional.empty();
        }

        CustomerProfileEntity entity = entityOptional.get();
        CustomerProfile domain = toDomain(entity);

        return Optional.of(domain);
    }

    @Override
    public Optional<CustomerProfile> findByUserId(Long userId) {
        return _customerProfileJpaRepository.findByUser_Id(userId)
                .map(CustomerProfileMapper::toDomain);
    }

    @Override
    public Optional<CustomerProfile> findByEmail(String email) {

        Optional<CustomerProfileEntity> entityOptional = _customerProfileJpaRepository.findByEmail(email);

        if (entityOptional.isEmpty()) {
            return Optional.empty();
        }

        CustomerProfileEntity entity = entityOptional.get();
        CustomerProfile domain = toDomain(entity);

        return Optional.of(domain);
    }

    @Override
    public CustomerProfile save(CustomerProfile profile) {
        CustomerProfileEntity entity = CustomerProfileMapper.toEntity(profile);
        CustomerProfileEntity saved = _customerProfileJpaRepository.save(entity);
        return toDomain(saved);
    }

    private CustomerProfile toDomain(CustomerProfileEntity e) {
        return new CustomerProfile(
                e.getId(),
                UserMapper.toDomain(e.getUser()),
                e.getFirstName(),
                e.getLastName(),
                e.getEmail(),
                e.getPhone(),
                e.getBirthday(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
