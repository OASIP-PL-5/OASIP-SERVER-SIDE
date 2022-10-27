package sit.int221.oasipservice.entities;

import javax.persistence.*;

@Entity
@Table(name = "event_category_owner", indexes = {
        @Index(name = "fk_event_category_has_user_event_category1_idx", columnList = "eventCategoryId"),
        @Index(name = "fk_event_category_has_user_user1_idx", columnList = "userId")
})
public class EventCategoryOwner {
    @EmbeddedId
    private EventCategoryOwnerId id;

    @MapsId("eventCategoryId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "eventCategoryId", nullable = false)
    private EventCategory eventCategory;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public EventCategory getEventCategory() {
        return eventCategory;
    }

    public void setEventCategory(EventCategory eventCategory) {
        this.eventCategory = eventCategory;
    }

    public EventCategoryOwnerId getId() {
        return id;
    }

    public void setId(EventCategoryOwnerId id) {
        this.id = id;
    }

}