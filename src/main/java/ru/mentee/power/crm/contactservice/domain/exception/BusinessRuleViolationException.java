package ru.mentee.power.crm.contactservice.domain.exception;

import java.util.UUID;
import lombok.Getter;

@Getter
public class BusinessRuleViolationException extends RuntimeException {
  private final String errorCode;

  private BusinessRuleViolationException(String message, String errorCode) {
    super(message);
    this.errorCode = errorCode;
  }

  public static BusinessRuleViolationException emailConflict(String email) {
    return new BusinessRuleViolationException(
        "Person with email " + email + " already exists", "PERSON_EMAIL_CONFLICT");
  }

  public static BusinessRuleViolationException innConflict(String inn) {
    return new BusinessRuleViolationException(
        "Company with inn " + inn + " already exists", "COMPANY_INN_CONFLICT");
  }

  public static BusinessRuleViolationException activeLinks(UUID id) {
    return new BusinessRuleViolationException(
        "Company with id " + id + " has active Person ↔ Company links", "COMPANY_HAS_LINKS");
  }

  public static BusinessRuleViolationException linkConflict(UUID personId, UUID companyId) {
    return new BusinessRuleViolationException(
        "Company with id " + companyId + " and Person with id " + personId + " already linked",
        "LINK_ALREADY_EXISTS");
  }
}
