package at.fhv.Event.infrastructure.persistence.booking;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingJpaRepository extends JpaRepository<BookingEntity, Long> {
}
