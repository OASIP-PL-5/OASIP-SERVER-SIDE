package sit.int221.oasipservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sit.int221.oasipservice.entities.Event;
import sit.int221.oasipservice.entities.User;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer>, JpaSpecificationExecutor<Event> {

    //filter:getByEventCategory
    @Query(
            value = "SELECT * FROM event e where e.eventCategoryId= :c ORDER BY eventStartTime DESC ", nativeQuery = true
    )
    List<Event> getByEventCategory(@Param("c") Integer eventCategoryId);

    @Query(
            value = "SELECT * FROM event e where e.eventCategoryId= :c and e.bookingEmail= :email ORDER BY e.eventStartTime DESC ", nativeQuery = true
    )
    List<Event> getByEventCategoryAndEmail(@Param("c") Integer eventCategoryId, @Param("email") String email);

    @Query(
            value = "select e.* from event e join event_category_owner eco on e.eventCategoryId = eco.eventCategoryId\n" +
                    "join user u on eco.userId = u.userId \n" +
                    "where e.eventCategoryId = :c and u.userId = :userId ORDER BY e.eventStartTime DESC", nativeQuery = true
    )
    List<Event> getByEventCategoryByCategoryOwner(@Param("c") Integer eventCategoryId, @Param("userId") Integer userId);


//    @Query(
//            value = "SELECT eventCategoryId FROM event_category_owner" , nativeQuery = true
//    )
//    List<Event> getByEventCategoryOwner();

    //    นำ query นี้ เพื่อให้ userid ลง param จะได้ eventLists ทั้งหมดที่สัมพันธ์กับ userId:lecturer นี้
    @Query
            (
                    value = "select * from event e join event_category ec on e.eventCategoryId = ec.eventCategoryId\n" +
                            "join event_category_owner eo on eo.eventCategoryId = ec.eventCategoryId\n" +
                            "join user u on eo.userId = u.userId\n" +
                            "where u.role = 'lecturer' and eo.userId = :userId " +
                            "ORDER BY e.eventStartTime DESC", nativeQuery = true)
    List<Event> findEventsFromUserId(@Param("userId") int userId);

    //    logic:: query เพื่อทำการ geteventdetail-bybookingId-lecturerId
    @Query
            (
                    value = "select * from event e join event_category ec on e.eventCategoryId = ec.eventCategoryId\n" +
                            "join event_category_owner eo on eo.eventCategoryId = ec.eventCategoryId\n" +
                            "join user u on eo.userId = u.userId\n" +
                            "where u.role = 'lecturer' and eo.userId = :userId and e.bookingId = :bookingId"
                    , nativeQuery = true)
    List<Event> findEventDetailFromUserIdAndBookingId(@Param("userId") int userId, Integer bookingId);

    //จัดการ All Event Date || upcoming
    @Query(value = "select e.* from event e where (DATE_ADD(e.eventStartTime,interval e.eventDuration minute)) >= now() ORDER BY e.eventStartTime DESC", nativeQuery = true)
    List<Event> getEventsByUpcoming();

    @Query(value = "select * from event e where (DATE_ADD(e.eventStartTime,interval e.eventDuration minute)) >= now() and e.bookingEmail= :email ORDER BY e.eventStartTime DESC", nativeQuery = true)
    List<Event> getEventsByUpcomingByEmail(@Param("email") String email);

    @Query(value = "select * from event e join event_category_owner eco on e.eventCategoryId = eco.eventCategoryId\n" +
            "join user u on eco.userId = u.userId \n" +
            "where (DATE_ADD(e.eventStartTime,interval e.eventDuration minute)) >= now() and u.userId = :userId ORDER BY e.eventStartTime DESC", nativeQuery = true)
    List<Event> getEventsByUpcomingByCategoryOwner(@Param("userId") Integer userId);

//    @Query(value = "select * from event e join event_category_owner eco on e.eventCategoryId = eco.eventCategoryId\n" +
//            "join user u on eco.userId = u.userId \n" +
//            "where (DATE_ADD(e.eventStartTime,interval e.eventDuration minute)) >= now() and u.userId = :userId", nativeQuery = true)

    //จัดการ All Event Date || past
    @Query(value = "select * from event e where (DATE_ADD(e.eventStartTime,interval e.eventDuration minute)) < now() ORDER BY e.eventStartTime DESC ;", nativeQuery = true)
    List<Event> getEventsByPast();

    @Query(value = "select * from event e where (DATE_ADD(e.eventStartTime,interval e.eventDuration minute)) < now() and e.bookingEmail= :email ORDER BY e.eventStartTime DESC", nativeQuery = true)
    List<Event> getEventsByPastByEmail(@Param("email") String email);

    @Query(value = "select * from event e join event_category_owner eco on e.eventCategoryId = eco.eventCategoryId\n" +
            "join user u on eco.userId = u.userId \n" +
            "where (DATE_ADD(e.eventStartTime,interval e.eventDuration minute)) < now() and u.userId = :userId ORDER BY e.eventStartTime DESC", nativeQuery = true)
    List<Event> getEventsByPastByCategoryOwner(@Param("userId") Integer userId);


    //    filter specific date
    @Query(value = "select * from event e where e.eventStartTime = :t ORDER BY e.eventStartTime DESC", nativeQuery = true)
    List<Event> getEventsByEventStartTime(@Param("t") String eventStartTime);

    @Query(value = "select * from event e where e.eventStartTime = :t and e.bookingEmail= :email ORDER BY e.eventStartTime DESC", nativeQuery = true)
    List<Event> getEventsByEventStartTimeByEmail(@Param("t") String eventStartTime, @Param("email") String email);

    @Query(
            value = "select e.* from event e join event_category_owner eco on e.eventCategoryId = eco.eventCategoryId\n" +
                    "join user u on eco.userId = u.userId \n" +
                    "where e.eventStartTime = :t and u.userId = :userId ORDER BY e.eventStartTime DESC", nativeQuery = true
    )
    List<Event> getEventsByEventStartTimeByCategoryOwner(@Param("t") String eventStartTime, @Param("userId") Integer userId);


    //get all event by email
    @Query(value = "select * from event e where e.bookingEmail = :e ORDER BY eventStartTime DESC", nativeQuery = true)
    List<Event> findByEmail(@Param("e") String email);

    //    get all event-start-time
    @Query(
            value = "SELECT * FROM event e  ORDER BY eventStartTime DESC ", nativeQuery = true
    )
    List<Event> getAllEvents();

    //    get all event-start-time
    @Query(
            value = "select * from event e where eventStartTime = :st", nativeQuery = true
    )
    List<Event> findEventByStartTime(@Param("st") LocalDateTime eventStartTime);

//    //    Event join File condition : BookingId
//    @Query(
//            value = "select * from event e join file f on e.bookingId = f.event_bookingId where e.bookingId = :e", nativeQuery = true
//    )
//    List<Event> findEventWithFileByEventId(@Param("e") Integer bookingId);

}