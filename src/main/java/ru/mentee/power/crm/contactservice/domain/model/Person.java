package ru.mentee.power.crm.contactservice.domain.model;

import java.util.UUID;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class Person {
  private UUID id;
  private String fullName;
  private String email;
  private String phone;

  public Person() {
    this.id = UUID.randomUUID();
  }

  public static Person create(String fullName, String email, String phone) {
    Person person = new Person();
    person.id = UUID.randomUUID();
    person.fullName = fullName;
    person.email = email;
    person.phone = phone;
    return person;
  }

}
