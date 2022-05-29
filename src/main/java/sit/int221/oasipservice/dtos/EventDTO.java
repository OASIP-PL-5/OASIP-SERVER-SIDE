package sit.int221.oasipservice.dtos;

import lombok.*;
import sit.int221.oasipservice.entities.EventCategory;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Data
public class EventDTO {
    private Integer id;
    private String bookingName;
    private String bookingEmail;
    private LocalDateTime eventStartTime;
    private Integer eventDuration;
    private String eventNotes;
    private Integer eventCategoryId;
    private String eventCategoryName;
}
