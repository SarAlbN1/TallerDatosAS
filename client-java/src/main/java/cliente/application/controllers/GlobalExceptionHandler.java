package cliente.application.controllers;

import cliente.application.exceptions.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<Map<String, Object>> handleBusiness(BusinessException ex) {
    log.error("Business error [{}]: {}", ex.code(), ex.getMessage());
    Map<String, Object> body = new HashMap<>();
    body.put("status", "FAILED");
    body.put("message", ex.getMessage());
    body.put("code", ex.code());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
    Map<String, Object> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
    Map<String, Object> body = new HashMap<>();
    body.put("status", "FAILED");
    body.put("message", "Validation error");
    body.put("errors", errors);
    return ResponseEntity.badRequest().body(body);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
    log.error("Unhandled error: {}", ex.getMessage());
    Map<String, Object> body = new HashMap<>();
    body.put("status", "ERROR");
    body.put("message", "Internal server error");
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
  }
}


