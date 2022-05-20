package sit.int221.oasipservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EditEventDTO {
    private Integer id;
//    private String bookingName;
//    private String bookingEmail;
    private LocalDateTime eventStartTime; //แก้ได้
//    private Integer eventDuration;
    private String eventNotes; //แก้ได้
//    private Integer eventCategoryId;
//    private String eventCategoryName;
}