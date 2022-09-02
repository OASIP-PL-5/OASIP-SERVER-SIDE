package sit.int221.oasipservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "never gonnagive you up")
public class ValidationEnum extends RuntimeException{

}