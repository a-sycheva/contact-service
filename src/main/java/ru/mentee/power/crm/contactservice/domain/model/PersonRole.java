package ru.mentee.power.crm.contactservice.domain.model;

public enum PersonRole {
  OWNER,
  EMPLOYEE;

  public static PersonRole fromString(String role) {
    if (role == null) {
      return null;
    }
    try {
      return PersonRole.valueOf(role);
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  public String toString() {
    return this.name();
  }
}
