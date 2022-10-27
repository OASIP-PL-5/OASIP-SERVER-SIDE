package sit.int221.oasipservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class ApiErrorInternalHandler {
    @ResponseBody
    @ExceptionHandler(value = {ApiRequestException.class})
    public ResponseEntity<Object> handleApiRequestException(ApiRequestException e) {
//1. create payload containing exceptions details
        HttpStatus badRequest = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiErrorInternal apiErrorInternal = new ApiErrorInternal(
                e.getMessage(),
                e,
                badRequest,
                ZonedDateTime.now(ZoneId.of("Z")) //อันนี้ timezone utc ลองตาม sample ไปก่อน
        );
//2. return response entity
        return new ResponseEntity<>(apiErrorInternal, badRequest);
    }
}
