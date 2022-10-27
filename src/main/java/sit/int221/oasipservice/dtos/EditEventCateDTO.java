package sit.int221.oasipservice.dtos;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EditEventCateDTO {
    private Integer eventCategoryId;
    private String eventCategoryName;
    private String eventCategoryDescription;
    private Integer eventDuration;
    private String catImg;
}
