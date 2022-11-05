package sit.int221.oasipservice.entities;

import javax.persistence.*;

@Entity
@Table(name = "files_management", uniqueConstraints = {
        @UniqueConstraint(name = "event_bookingId_UNIQUE", columnNames = {"event_bookingId"})
})
public class FilesManagement {
    @Id
    @Column(name = "event_bookingId", nullable = false)
    private Integer id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_bookingId", nullable = false)
    private Event event;

    @Column(name = "fileName", nullable = false, length = 100)
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}