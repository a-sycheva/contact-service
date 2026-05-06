package ru.mentee.power.crm.contactservice.domain.exception;

public class ExternalServiceUnavailableException extends RuntimeException {
  public ExternalServiceUnavailableException(String message) {
    super(message);
  }
}
