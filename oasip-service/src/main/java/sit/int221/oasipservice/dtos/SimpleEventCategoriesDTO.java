package sit.int221.oasipservice.dtos;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimpleEventCategoriesDTO {
    private Integer eventCategoryId;
    private String eventCategoryName;
    private String eventCategoryDescription;
    private Integer eventDuration;
}
