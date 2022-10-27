package sit.int221.oasipservice.entities;

import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class EventCategoryOwnerId implements Serializable {
    private static final long serialVersionUID = 6060333350864338564L;
    @Column(name = "eventCategoryId", nullable = false)
    private Integer eventCategoryId;
    @Column(name = "userId", nullable = false)
    private Integer userId;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getEventCategoryId() {
        return eventCategoryId;
    }

    public void setEventCategoryId(Integer eventCategoryId) {
        this.eventCategoryId = eventCategoryId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, eventCategoryId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        EventCategoryOwnerId entity = (EventCategoryOwnerId) o;
        return Objects.equals(this.userId, entity.userId) &&
                Objects.equals(this.eventCategoryId, entity.eventCategoryId);
    }
}