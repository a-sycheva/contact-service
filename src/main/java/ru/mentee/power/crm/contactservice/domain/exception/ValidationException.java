package ru.mentee.power.crm.contactservice.domain.exception;

public class ValidationException extends RuntimeException {
  public ValidationException(String message) {
    super(message);
  }
}
