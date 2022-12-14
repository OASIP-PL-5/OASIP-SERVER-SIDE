package sit.int221.oasipservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sit.int221.oasipservice.entities.Event;
import sit.int221.oasipservice.entities.File;

import java.util.List;

public interface FileRepository extends JpaRepository<File, String> {

    @Query(
            value = "SELECT * FROM file WHERE event_bookingId = :id ", nativeQuery = true
    )
    File getFileByBookingId(@Param("id") Integer event_bookingId);

    // update file
    @Modifying
    @Query(
            value = "update file set fileName = :fileName, fileType = :fileType, data = :data where fileId = :fileId", nativeQuery = true
    )
    List<File> updateFile(@Param("fileId") String fileId, @Param("fileName") String fileName, @Param("fileType") String fileType, @Param("data") byte[] data);

    //    delete file by bookingId
    @Query(
            value = "delete from file WHERE event_bookingId = :bookId ", nativeQuery = true
    )
    File deleteFileByBookingId(@Param("bookId") Integer bookId);

    //    get data from relation of usre,event,file
    @Query(
            value = "select * from file f join event e on e.bookingId = f.event_bookingId " +
                    "join user u on u.email = e.bookingEmail " +
                    "where u.email = :ue and f.event_bookingId= :fileEventId", nativeQuery = true
    )
    List<File> getDataByEmailAndBookingIdWithFile(@Param("ue") String userEmail, @Param("fileEventId") Integer fileEventId);

    //    get data from relation of usre,event,file
    @Query(
            value = "select * from file f join event e on e.bookingId = f.event_bookingId " +
                    "join user u on u.email = e.bookingEmail " +
                    "where u.email = :ue and f.fileId= :fileId", nativeQuery = true
    )
    List<File> getDataByEmailAndFileId(@Param("ue") String userEmail, @Param("fileId") String fileId);
}
