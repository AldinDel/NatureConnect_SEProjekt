package at.fhv.Event.persistence;

import at.fhv.Event.domain.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
}

//public interface EventRepository extends JpaRepository<>{}
