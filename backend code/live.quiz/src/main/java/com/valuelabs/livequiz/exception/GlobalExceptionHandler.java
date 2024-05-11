package com.valuelabs.livequiz.exception;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.LinkedHashMap;
import java.util.Map;
/**
 * GlobalExceptionHandler provides centralized exception handling for all controllers.
 */

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * Handles ResourceNotFoundException by logging the error and returning a response with HTTP status NOT_FOUND.
     *
     * @param exception The ResourceNotFoundException instance.
     * @return ResponseEntity with an error body and NOT_FOUND status.
     */

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException exception) {
        log.error("Resource not found exception is thrown!");
        return new ResponseEntity<>(getErrorBody(exception.getResourceName(), exception.getMessage()), HttpStatus.NOT_FOUND);
    }
    /**
     * Handles ResourceExistsException by logging the error and returning a response with HTTP status CONFLICT.
     *
     * @param exception The ResourceExistsException instance.
     * @return ResponseEntity with an error body and CONFLICT status.
     */

    @ExceptionHandler(ResourceExistsException.class)
    public ResponseEntity<Object> handleResourceExistsException(ResourceExistsException exception) {
        log.error("Resource exists exception is thrown!");
        return new ResponseEntity<>(getErrorBody(exception.getResourceName(), exception.getMessage()), HttpStatus.CONFLICT);
    }
    /**
     * Handles InvalidResourceDetailsException by logging the error and returning a response with HTTP status BAD_REQUEST.
     *
     * @param exception The InvalidResourceDetailsException instance.
     * @return ResponseEntity with an error body and BAD_REQUEST status.
     */

    @ExceptionHandler(InvalidResourceDetailsException.class)
    public ResponseEntity<Object> handleInvalidResourceDetailsException(InvalidResourceDetailsException exception) {
        log.error("Invalid Resource Details exception is thrown!");
        return new ResponseEntity<>(getErrorBody(exception.getResourceName(), exception.getMessage()), HttpStatus.BAD_REQUEST);
    }
    /**
     * Handles CustomJwtException by logging the error and returning a response with HTTP status BAD_REQUEST.
     *
     * @param exception The CustomJwtException instance.
     * @return ResponseEntity with an error body and BAD_REQUEST status.
     */

    @ExceptionHandler(CustomJwtException.class)
    public ResponseEntity<Object> handleCustomJwtException(CustomJwtException exception) {
        log.error("Custom JWT Exception is thrown !" + exception.getMessage());
        return new ResponseEntity<>(getErrorBody(exception.getResourceName(), exception.getMessage()), HttpStatus.BAD_REQUEST);
    }
    /**
     * Constructs and returns an error body with the provided resource name and message.
     *
     * @param resourceName The resource name associated with the exception.
     * @param message      The detail message.
     * @return Map representing the error body.
     */

    public Map<Object, Object> getErrorBody(Object resourceName, Object message){
        Map<Object, Object> errorBody = new LinkedHashMap<>();
        errorBody.put("entity", resourceName);
        errorBody.put("message", message);
        return errorBody;
    }
}
