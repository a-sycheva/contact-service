package ru.mentee.power.crm.contactservice.adapter.in.rest.error;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.mentee.power.crm.contactservice.adapter.in.rest.dto.Problem;
import ru.mentee.power.crm.contactservice.domain.exception.BusinessRuleViolationException;
import ru.mentee.power.crm.contactservice.domain.exception.EntityNotFoundException;
import ru.mentee.power.crm.contactservice.domain.exception.ValidationException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(BusinessRuleViolationException.class)
  public ResponseEntity<Problem> handleBusinessRuleViolation(
      BusinessRuleViolationException ex, HttpServletRequest request) {

    Problem problem =
        createProblem(
            URI.create("/problems/conflict"),
            "Duplicate Entity",
            409,
            ex.getMessage(),
            request.getRequestURI(),
            ex.getErrorCode());

    log.warn("Business rule violation: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<Problem> handleEntityNotFound(
      EntityNotFoundException ex, HttpServletRequest request) {

    Problem problem =
        createProblem(
            URI.create("/problems/not-found"),
            "Entity Not Found",
            404,
            ex.getMessage(),
            request.getRequestURI(),
            ex.getErrorCode());

    log.warn("Entity not found: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
  }

  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<Problem> handleValidation(
      ValidationException ex, HttpServletRequest request) {

    Problem problem =
        createProblem(
            URI.create("/problems/validation"),
            "Validation Error",
            400,
            ex.getMessage(),
            request.getRequestURI(),
            "VALIDATION_ERROR");

    log.warn("Validation Error: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Problem> handleConstraintViolation(
      ConstraintViolationException ex, HttpServletRequest request) {

    log.warn("Constraint violation: {}", ex.getMessage());

    String detail =
        ex.getConstraintViolations().stream()
            .map(
                violation -> {
                  String path = violation.getPropertyPath().toString();
                  String field =
                      path.contains(".") ? path.substring(path.lastIndexOf('.') + 1) : path;
                  return field + ": " + violation.getMessage();
                })
            .collect(Collectors.joining(", "));

    Problem problem =
        createProblem(
            URI.create("/problems/validation"),
            "Validation Error",
            400,
            detail,
            request.getRequestURI(),
            "VALIDATION_ERROR");

    return ResponseEntity.badRequest().body(problem);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {

    String detail =
        ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));

    Problem problem =
        createProblem(
            URI.create("/problems/validation"),
            "Validation Error",
            400,
            detail,
            ((ServletRequestAttributes) request).getRequest().getRequestURI(),
            "VALIDATION_ERROR");

    log.error("Validation Error: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Problem> handleGenericException(Exception ex, HttpServletRequest request) {

    log.error("Unexpected error: {}", ex.getMessage(), ex);

    Problem problem =
        createProblem(
            URI.create("/problems/internal-error"),
            "Internal Server Error",
            500,
            "An unexpected error occurred",
            request.getRequestURI(),
            "INTERNAL_ERROR");
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
  }

  private Problem createProblem(
      URI type, String title, int status, String detail, String instance, String errorCode) {

    Problem problem = new Problem();

    problem.setType(type);
    problem.setTitle(title);
    problem.setStatus(status);
    problem.setDetail(detail);
    problem.setInstance(instance);
    problem.setErrorCode(errorCode);
    problem.setService("contact-service");
    problem.setTimestamp(LocalDateTime.now());
    problem.setTraceId(null);

    return problem;
  }
}
