package rht.samples.spring.rest;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/*
 * This class sets up exception handlers for all REST endpoints (all RestControllers) within the application.
 */
@RestControllerAdvice
public class DemoControllerAdvice {

  static final Logger logger = LoggerFactory.getLogger(DemoControllerAdvice.class);

  /*
   * Handle a ICatalogService EntryNotFoundException.
   */
  @ExceptionHandler(value = EntryNotFoundException.class)
  public ResponseEntity<Map<String, String>> handleEntryNotFoundException(EntryNotFoundException ex) {

    logger.warn("handling exception => " + ex.toString());

    Map<String, String> errorResponse = new HashMap<String, String>();
    errorResponse.put("errorMessage", ex.getMessage());

    return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorResponse);
  }

  /*
   * Handle any other Exception. Assume this is not intentional, and
   * log an exception stack trace.
   */
  @ExceptionHandler(value = Exception.class)
  public ResponseEntity<Map<String, String>> handleException(Exception ex) {

    logger.warn("handling exception", ex);

    Map<String, String> errorResponse = new HashMap<String, String>();
    errorResponse.put("errorMessage", ex.toString());

    return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorResponse);
  }
}
