package sit.int221.oasipservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sit.int221.oasipservice.entities.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer>, JpaSpecificationExecutor<Event> {

    @Query(
            value = "SELECT * FROM event e where e.eventCategoryId= :c ORDER BY eventStartTime DESC ", nativeQuery = true
    )
    List<Event> getByEventCategory(@Param("c") Integer eventCategoryId);

    //จัดการ All Event Date || upcoming
    @Query(value = "select * from event e where (DATE_ADD(e.eventStartTime,interval e.eventDuration minute)) >= now()", nativeQuery = true)
    List<Event> getEventsByUpcoming();

    //จัดการ All Event Date || past
    @Query(value = "select * from event e where (DATE_ADD(e.eventStartTime,interval e.eventDuration minute)) < now() ORDER BY e.eventStartTime DESC ;", nativeQuery = true)
    List<Event> getEventsByPast();

    //    filter specific date
    @Query(value = "select * from event e where e.eventStartTime = :t", nativeQuery = true)
    List<Event> getEventsByEventStartTime(@Param("t") String eventStartTime);

    //get all event by email
    @Query(value = "select * from event e where e.bookingEmail = :e", nativeQuery = true)
    List<Event> findByEmail(@Param("e") String email);
}