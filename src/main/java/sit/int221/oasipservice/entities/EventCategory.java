package sit.int221.oasipservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.*;
import javax.validation.Constraint;
import javax.validation.constraints.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "event_category")

public class EventCategory {
    @Id
    @Column(name = "eventCategoryId", nullable = false)
    private Integer id;

    //    @UniqueElements(message = "Clinic Name must be unique")
    @NotBlank(message = "Clinic Name can not be empty")
    @NotNull(message = "Clinic Name can not be null")
    @Column(name = "eventCategoryName", nullable = false, length = 100, unique = true)
    private String eventCategoryName;

    //    @NotBlank(message = "Clinic Description can not be empty")
//    @NotNull(message = "Clinic Description can not be null")
    @Size(max = 500, message = "Description max is 500 character")
    @Column(name = "eventCategoryDescription")
    private String eventCategoryDescription;


    @NotNull(message = "Duration can not be null.")
//    @Size(min = 1, max = 480, message = "Duration must be between 1 and 480 minutes.")
    @Range(min = 1, max = 480, message = "Duration must be between 1 and 480 minutes.")
    @Column(name = "eventDuration", nullable = false)
    private Integer eventDuration;

    @JsonIgnore
    @OneToMany(mappedBy = "eventCategory")
    private Set<Event> events = new LinkedHashSet<>();

    @Column(name = "categoryImg", nullable = false)
    private String catImg;

    public String getCatImg() {
        return catImg;
    }

    public void setCatImg(String catImg) {
        this.catImg = catImg;
    }

    public Set<Event> getEvents() {
        return events;
    }

    public void setEvents(Set<Event> events) {
        this.events = events;
    }

    public Integer getEventDuration() {
        return eventDuration;
    }

    public void setEventDuration(Integer eventDuration) {
        this.eventDuration = eventDuration;
    }

    public String getEventCategoryDescription() {
        return eventCategoryDescription;
    }

    public void setEventCategoryDescription(String eventCategoryDescription) {
        this.eventCategoryDescription = eventCategoryDescription;
    }

    public String getEventCategoryName() {
        return eventCategoryName;
    }

    public void setEventCategoryName(String eventCategoryName) {
        this.eventCategoryName = eventCategoryName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}