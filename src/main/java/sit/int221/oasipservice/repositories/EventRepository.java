package sit.int221.oasipservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import sit.int221.oasipservice.entities.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event,Integer> {
//    List<Event> findTopByIdOrderByIdDesc(Integer bookingId);
//    @Query เพื่อกรอง dateTime-overlap
//     @Query("DATE_ADD(eventStartTime,interval eventDuration minute) from Event")
//     List<Event> findAll();
//     public LocalDateTime getAllByEventStartTimeLessThanEqual(LocalDateTime eventStartTime);
//    public List<Event> findAllByEventCategoryNameOrderByEventCategoryNameDesc(String name);

}
