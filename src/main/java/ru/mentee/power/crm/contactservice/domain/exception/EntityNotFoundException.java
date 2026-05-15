package ru.mentee.power.crm.contactservice.domain.exception;

import java.util.UUID;
import lombok.Getter;

@Getter
public class EntityNotFoundException extends RuntimeException {
  private final String errorCode;

  private EntityNotFoundException(String message, String errorCode) {
    super(message);
    this.errorCode = errorCode;
  }

  public static EntityNotFoundException forPerson(UUID id) {
    return new EntityNotFoundException("Person with id " + id + " not found ", "PERSON_NOT_FOUND");
  }

  public static EntityNotFoundException forCompany(UUID id) {
    return new EntityNotFoundException("Company with id " + id + " not found", "COMPANY_NOT_FOUND");
  }

  public static EntityNotFoundException forLink(UUID personId, UUID companyId) {
    return new EntityNotFoundException(
        "Link with Company id " + companyId + " and Person id" + personId + " not found",
        "LINK_NOT_FOUND");
  }
}
