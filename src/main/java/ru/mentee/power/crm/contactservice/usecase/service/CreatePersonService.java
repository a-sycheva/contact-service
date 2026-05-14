package ru.mentee.power.crm.contactservice.usecase.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.contactservice.domain.exception.BusinessRuleViolationException;
import ru.mentee.power.crm.contactservice.domain.model.Person;
import ru.mentee.power.crm.contactservice.usecase.port.in.CreatePersonUseCase;
import ru.mentee.power.crm.contactservice.usecase.port.out.PersonOutPort;

@Service
@RequiredArgsConstructor
public class CreatePersonService implements CreatePersonUseCase {
  private final PersonOutPort personOutPort;

  @Override
  @Transactional
  public Person create(Person person) {
    if (personOutPort.existsByEmail(person.getEmail())) {
      throw BusinessRuleViolationException.emailConflict(person.getEmail());
    }

    return personOutPort.save(person);
  }
}
