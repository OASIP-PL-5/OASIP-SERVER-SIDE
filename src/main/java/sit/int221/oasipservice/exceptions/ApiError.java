package sit.int221.oasipservice.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ApiError {

    private LocalDateTime timestamp;
    private HttpStatus status;
    private String message ;
    private List<String> error_detail;


    public ApiError(LocalDateTime timestamp,HttpStatus status, String message, List<String> error_detail) {
        super();
        this.timestamp = timestamp;
        this.status = status;
        this.message = "Validation failed.";
        this.error_detail = error_detail;
    }




//    เหมือนข้างล่างจะไม่ทำงาน
//    public ApiError(HttpStatus status, String message, String error) {
//        super();
//        this.status = status;
//        this.message = message = "test";
//        error_detail = Arrays.asList(error);
//    }

}
