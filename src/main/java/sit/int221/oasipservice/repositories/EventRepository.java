package sit.int221.oasipservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sit.int221.oasipservice.entities.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Integer> {

    @Query(
            value = "SELECT * FROM event e where e.eventCategoryId= :c ORDER BY eventStartTime DESC ", nativeQuery = true
    )
    public List<Event> getByEventCategory(@Param("c") Integer eventCategoryId);

    //จัดการ All Event Date || upcoming
    @Query(value = "select * from event e where (DATE_ADD(e.eventStartTime,interval e.eventDuration minute)) >= now()", nativeQuery = true)
    public List<Event> getEventsByUpcoming();

    //จัดการ All Event Date || past
    @Query(value = "select * from event e where (DATE_ADD(e.eventStartTime,interval e.eventDuration minute)) < now() ORDER BY e.eventStartTime DESC ;", nativeQuery = true)
    public List<Event> getEventsByPast();


}