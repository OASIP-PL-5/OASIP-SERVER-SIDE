//package sit.int221.oasipservice.entities;
//
//import org.hibernate.Hibernate;
//
//import javax.persistence.Column;
//import javax.persistence.Embeddable;
//import javax.persistence.Entity;
//import java.io.Serializable;
//import java.util.Objects;
//
//@Embeddable
//public class FileId implements Serializable {
//    private static final long serialVersionUID = 8597527762558106110L;
//    @Column(name = "fileId", nullable = false, length = 200)
//    private String fileId;
//    @Column(name = "event_bookingId", nullable = false)
//    private Integer eventBookingid;
//
//    public Integer getEventBookingid() {
//        return eventBookingid;
//    }
//
//    public void setEventBookingid(Integer eventBookingid) {
//        this.eventBookingid = eventBookingid;
//    }
//
//    public String getFileId() {
//        return fileId;
//    }
//
//    public void setFileId(String fileId) {
//        this.fileId = fileId;
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(eventBookingid, fileId);
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
//        FileId entity = (FileId) o;
//        return Objects.equals(this.eventBookingid, entity.eventBookingid) &&
//                Objects.equals(this.fileId, entity.fileId);
//    }
//}