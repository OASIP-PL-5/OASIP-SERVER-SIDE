package sit.int221.oasipservice.exceptions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.List;

@Getter @Setter
public class ApiErrorInternal   {

    private final String message;
    private final Throwable throwable;
    private final HttpStatus httpStatus;
    private final ZonedDateTime timestamp;

    public ApiErrorInternal(String message, Throwable throwable, HttpStatus httpStatus, ZonedDateTime timestamp) {
        this.message = message;
        this.throwable = throwable;
        this.httpStatus = httpStatus;
        this.timestamp = timestamp;
    }


//    private Timestamp timestamp;
//    private HttpStatus status;
//    private String message ;
//    private List<String> error_detail;
//    private String path;
//
//    public ApiErrorInternal(Timestamp timestamp,HttpStatus status, String message, List<String> error_detail, String path) {
//        super();
//        this.timestamp = timestamp;
//        this.status = status;
//        this.message = "Validation failed.";
//        this.error_detail = error_detail;
//        this.path = path;
//    }
}
