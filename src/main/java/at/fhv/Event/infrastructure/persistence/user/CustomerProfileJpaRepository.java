package at.fhv.Event.infrastructure.persistence.user;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CustomerProfileJpaRepository extends JpaRepository<CustomerProfileEntity, Long> {
    Optional<CustomerProfileEntity> findByEmail(String email);
}