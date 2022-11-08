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

}

