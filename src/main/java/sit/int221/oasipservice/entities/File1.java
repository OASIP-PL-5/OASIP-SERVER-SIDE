//package sit.int221.oasipservice.entities;
//
//import lombok.AllArgsConstructor;
//import lombok.NoArgsConstructor;
//
//import javax.persistence.*;
//
//
//@Entity
//@Table(name = "file", indexes = {
//        @Index(name = "fk_file_event_idx", columnList = "event_bookingId")
//})
//public class File {
//    @EmbeddedId
//    private FileId id;
//
//    @MapsId("eventBookingid")
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "event_bookingId", nullable = false)
//    private Event eventBooking;
//
//    @Column(name = "fileType", length = 45)
//    private String fileType;
//
//    @Column(name = "fileName", length = 45)
//    private String fileName;
//
//    @Column(name = "data")
//    private byte[] data;
//
//
//
//    public File(String fileName, String fileType, byte[] data) {
//        this.fileName = fileName;
//        this.fileType = fileType;
//        this.data = data;
//    }
//
//    public File() {
//
//    }
//
//    public byte[] getData() {
//        return data;
//    }
//
//    public void setData(byte[] data) {
//        this.data = data;
//    }
//
//    public String getFileName() {
//        return fileName;
//    }
//
//    public void setFileName(String fileName) {
//        this.fileName = fileName;
//    }
//
//    public String getFileType() {
//        return fileType;
//    }
//
//    public void setFileType(String fileType) {
//        this.fileType = fileType;
//    }
//
//    public Event getEventBooking() {
//        return eventBooking;
//    }
//
//    public void setEventBooking(Event eventBooking) {
//        this.eventBooking = eventBooking;
//    }
//
//    public FileId getId() {
//        return id;
//    }
//
//    public void setId(FileId id) {
//        this.id = id;
//    }
//}