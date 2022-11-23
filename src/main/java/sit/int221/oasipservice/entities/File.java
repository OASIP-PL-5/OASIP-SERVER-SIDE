package sit.int221.oasipservice.entities;

import org.hibernate.annotations.GenericGenerator;
import sit.int221.oasipservice.entities.Event;

import javax.persistence.*;
import java.util.Optional;

@Entity
@Table(name = "file")
public class File {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "fileId", nullable = false, length = 200)
    private String id;

    //    @OneToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "event_bookingId", nullable = false)
    @Column(name = "event_bookingId")
    private Integer eventBooking;

    @Column(name = "fileType", length = 45)
    private String fileType;

    @Column(name = "fileName", length = 45)
    private String fileName;

    @Column(name = "data")
    private byte[] data;

    public File(){}

    public File(Integer eventBooking, String fileName, String fileType, byte[] data) {
        this.eventBooking = eventBooking;
        this.fileName = fileName;
        this.fileType = fileType;
        this.data = data;
    }


    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Integer getEventBooking() {
        return eventBooking;
    }

    public void setEventBooking(Integer eventBooking) {
        this.eventBooking = eventBooking;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}