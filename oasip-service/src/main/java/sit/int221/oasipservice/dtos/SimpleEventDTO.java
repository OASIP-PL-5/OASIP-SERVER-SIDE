package sit.int221.oasipservice.dtos;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;


@Getter @Setter
public class SimpleEventDTO {
    private Integer bookingId;   //null
    private String bookingName;
    private String bookingEmail;
    private Timestamp eventStartTime; //null
    private Integer eventDuration;
    private String eventNotes;
    private Integer eventCategoryId;
    private String eventCategory;
}
