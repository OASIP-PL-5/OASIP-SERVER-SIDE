package sit.int221.oasipservice.dtos;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDTO {
    private String bookingName;
    private String bookingEmail;
    private LocalDateTime eventStartTime;
    private Integer eventDuration;
    private String eventNotes;
    private String eventCategoryName;
    private Integer eventCategoryId;
}