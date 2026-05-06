package ru.mentee.power.crm.contactservice.usecase.service;

import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import ru.mentee.power.crm.contactservice.domain.exception.BusinessRuleViolationException;
import ru.mentee.power.crm.contactservice.domain.model.Person;
import ru.mentee.power.crm.contactservice.usecase.port.in.CreatePersonUseCase;
import ru.mentee.power.crm.contactservice.usecase.port.out.PersonOutPort;

@Service
@RequiredArgsConstructor
public class CreatePersonService implements CreatePersonUseCase {
  private final PersonOutPort personOutPort;

  @Override
  public Person create(Person person) {
    if (personOutPort.existsByEmail(person.getEmail())) {
      throw new BusinessRuleViolationException("Person with email " + person.getEmail() + " already exists");
    }

    return personOutPort.save(person);
  }
}
