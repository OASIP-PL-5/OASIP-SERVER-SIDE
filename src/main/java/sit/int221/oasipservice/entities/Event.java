package sit.int221.oasipservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.persistence.*;
import javax.servlet.annotation.HttpConstraint;
import javax.validation.constraints.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "event")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookingId", nullable = false)
    private Integer id;

    @NotBlank(message = "Booking name must not be empty.")
    @NotNull(message = "Booking name is required.")
    @Column(name = "bookingName", nullable = false, length = 100)
    private String bookingName;

    @NotBlank(message = "Booking email must not be empty.")
    @NotNull(message = "Booking email is required.")
    @Email(message = "Please insert a valid email format.")
    @Column(name = "bookingEmail", nullable = false, length = 50)
    private String bookingEmail;

    @NotNull(message = "Please choose your appointment date.")
    @FutureOrPresent(message = "Booking date must be future or present.")
    @Column(name = "eventStartTime", nullable = false)
    private LocalDateTime eventStartTime;

    @Column(name = "eventDuration", nullable = false)
    private Integer eventDuration;

    @Column(name = "eventNotes", nullable = true, length = 500)
    private String eventNotes;

    @JsonIgnore
    @NotNull(message = "Please choose Clinic Category date.")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "eventCategoryId", nullable = false)
    private EventCategory eventCategory;

    @Column(name = "eventCategory", nullable = false, length = 100)
    private String eventCategoryName;

    public String getEventCategoryName() {
        return eventCategoryName;
    }

    public void setEventCategoryName(String eventCategoryName) {
        this.eventCategoryName = eventCategoryName;
    }

    public EventCategory getEventCategory() {
        return eventCategory;
    }

    public void setEventCategory(EventCategory eventCategory) {
        this.eventCategory = eventCategory;
    }

    public String getEventNotes() {
        return eventNotes;
    }

    public void setEventNotes(String eventNotes) {
        this.eventNotes = eventNotes;
    }

    public Integer getEventDuration() {
        return eventDuration;
    }

    public void setEventDuration(Integer eventDuration) {
        this.eventDuration = eventDuration;
    }

    public LocalDateTime getEventStartTime() {
        return eventStartTime;
    }

    public void setEventStartTime(LocalDateTime eventStartTime) {
        this.eventStartTime = eventStartTime;
    }

    public String getBookingEmail() {
        return bookingEmail;
    }

    public void setBookingEmail(String bookingEmail) {
        this.bookingEmail = bookingEmail;
    }

    public String getBookingName() {
        return bookingName;
    }

    public void setBookingName(String bookingName) {
        this.bookingName = bookingName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


}