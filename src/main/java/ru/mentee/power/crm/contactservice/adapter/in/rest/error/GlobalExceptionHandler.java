package ru.mentee.power.crm.contactservice.adapter.in.rest.error;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import ru.mentee.power.crm.contactservice.domain.exception.ExternalServiceUnavailableException;
import ru.mentee.power.crm.contactservice.domain.exception.ValidationException;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
  private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);



  @ExceptionHandler(BusinessRuleViolationException.class)
  public ResponseEntity<Problem> handleBusinessRuleViolation(
      BusinessRuleViolationException ex,
      HttpServletRequest request) {

    Problem problem = createProblem(
        URI.create("/problems/conflict"),
        "Duplicate Entity",
        409,
        ex.getMessage(),
        request.getRequestURI(),
        "PERSON_EMAIL_CONFLICT");

    LOG.warn("Business rule violation: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<Problem> handleEntityNotFound(
      EntityNotFoundException ex,
      HttpServletRequest request) {

    Problem problem = createProblem(
        URI.create("/problems/not-found"),
        "Entity Not Found",
        404,
        ex.getMessage(),
        request.getRequestURI(),
        "PERSON_NOT_FOUND");

    LOG.warn("Entity not found: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
  }

  @ExceptionHandler(ExternalServiceUnavailableException.class)
  public ResponseEntity<Problem> handleExternalServiceUnavailable(
      ExternalServiceUnavailableException ex,
      HttpServletRequest request) {

    Problem problem = createProblem(
        URI.create("/problems/external-service-unavailable"),
        "External Service Unavailable",
        503,
        ex.getMessage(),
        request.getRequestURI(),
        "SERVICE_UNAVAILABLE");

    LOG.error("External service unavailable: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(problem);
  }

  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<Problem> handleValidation(
      ValidationException ex,
      HttpServletRequest request) {

    Problem problem = createProblem(
        URI.create("/problems/validation"),
        "Validation Error",
        400,
        ex.getMessage(),
        request.getRequestURI(),
        "VALIDATION_ERROR");

    LOG.error("Validation Error: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

    String detail = ex.getBindingResult().getFieldErrors().stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .collect(Collectors.joining(", "));

    Problem problem = createProblem(
        URI.create("/problems/validation"),
        "Validation Error",
        400,
        detail,
        ((ServletRequestAttributes) request).getRequest().getRequestURI(),
        "VALIDATION_ERROR");

    LOG.error("Validation Error: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
  }

  private Problem createProblem(URI type,
                                String title,
                                int status,
                                String detail,
                                String instance,
                                String errorCode) {

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