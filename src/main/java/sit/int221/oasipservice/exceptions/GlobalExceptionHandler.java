package sit.int221.oasipservice.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import sit.int221.oasipservice.exceptions.ApiError;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

//    @Override
//    protected ResponseEntity<Object> handleExceptionInternal (Exception ex,Object body, HttpHeaders headers, HttpStatus status, WebRequest request){
//        ApiError error = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage());
//        return super.handleExceptionInternal(ex,error,headers,status,request);
//    }

//    @Override
//    protected ResponseEntity<Object> handleMethodArgumentNotValid(
//            MethodArgumentNotValidException ex, HttpHeaders headers,
//            HttpStatus status, WebRequest request) {
//
//        List<String> errors = new ArrayList<String>();
//        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
//            errors.add(error.getField() + ": " + error.getDefaultMessage());
//        }
//        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
//            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
//        }
//        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage(), errors);
//        return handleExceptionInternal(ex, apiError,  headers, apiError.getStatus(), request );
//    }

//    @Override
//    protected ResponseEntity<Object> handleMissingServletRequestParameter(
//            MissingServletRequestParameterException ex, HttpHeaders headers,
//            HttpStatus status, WebRequest request) {
//        String error = ex.getParameterName() + " parameter is missing";
//
//        ApiError apiError =
//                new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
//        return new ResponseEntity<Object>(
//                apiError, new HttpHeaders(), apiError.getStatus());
//    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {

        // สรุปได้ว่า ตรงส่วนนี้ทำงานเกี่ยวกับ "errors"
        List<String> errors = new ArrayList<String>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(violation.getPropertyPath() + ": " + violation.getMessageTemplate());
        }


        ApiError apiError = new ApiError(LocalDateTime.now(),HttpStatus.BAD_REQUEST,ex.getLocalizedMessage(),errors);
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());

//        ApiError apiError500 = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR,ex.getLocalizedMessage(),errors);
//        return new ResponseEntity<Object>(apiError500, new HttpHeaders(), apiError500.getStatus());
    }




//    @ExceptionHandler({ MethodArgumentTypeMismatchException.class })
//    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(
//            MethodArgumentTypeMismatchException ex, WebRequest request) {
//        String error =
//                ex.getName() + " should be of type " + ex.getRequiredType().getName();
//
//        ApiError apiError =
//                new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
//        return new ResponseEntity<Object>(
//                apiError, new HttpHeaders(), apiError.getStatus());
//    }

}
