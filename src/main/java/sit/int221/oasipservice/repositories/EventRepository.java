package sit.int221.oasipservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.oasipservice.entities.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event,Integer> {
    List<Event> findTopByIdOrderByIdDesc(Integer bookingId);
}
