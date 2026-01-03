package at.fhv.Event.domain.model.user;

import java.util.Optional;

public interface CustomerProfileRepository {
    Optional<CustomerProfile> findById(Long id);
    Optional<CustomerProfile> findByEmail(String email);
    Optional<CustomerProfile> findByUserId(Long userId);
    CustomerProfile save(CustomerProfile profile);
}
