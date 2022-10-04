package sit.int221.oasipservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.context.SecurityContextHolder;
import sit.int221.oasipservice.entities.Event;
import sit.int221.oasipservice.entities.EventCategoryOwner;

import java.util.List;

public interface EventCategoryOwnerRepository extends JpaRepository<EventCategoryOwner, Integer> {
    @Query
            (
                    value = "SELECT * FROM event_category_owner eo where eo.userId = :userId", nativeQuery = true)
    List<EventCategoryOwner> findByUserid(@Param("userId") int userId);


////    นำ query นี้ เพื่อให้ userid ลง param จะได้ eventLists ทั้งหมดที่สัมพันธ์กับ userId:lecturer นี้
//    @Query
//            (
//                    value = "select eo.eventCategoryId,ec.eventCategoryName,u.email from event e join event_category ec on e.eventCategoryId = ec.eventCategoryId\n" +
//                            "join event_category_owner eo on eo.eventCategoryId = ec.eventCategoryId\n" +
//                            "join user u on eo.userId = u.userId\n" +
//                            "where u.role = 'lecturer' and eo.userId = :userId", nativeQuery = true)
//    List<EventCategoryOwner> findEventsFromUserId(@Param("userId") int userId);

//    @Query(
//            value = "SELECT * FROM event e where e.eventCategoryId= :c ORDER BY eventStartTime DESC ", nativeQuery = true
//    )
//    List<Event> getByEventCategory(@Param("c") Integer eventCategoryId);
}
