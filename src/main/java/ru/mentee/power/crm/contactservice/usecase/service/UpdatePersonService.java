package ru.mentee.power.crm.contactservice.usecase.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mentee.power.crm.contactservice.domain.exception.BusinessRuleViolationException;
import ru.mentee.power.crm.contactservice.domain.exception.EntityNotFoundException;
import ru.mentee.power.crm.contactservice.domain.model.Person;
import ru.mentee.power.crm.contactservice.usecase.port.in.UpdatePersonUseCase;
import ru.mentee.power.crm.contactservice.usecase.port.out.PersonOutPort;

@Service
@RequiredArgsConstructor
public class UpdatePersonService implements UpdatePersonUseCase {
  private final PersonOutPort personOutPort;

  @Override
  public Person updatePerson(UUID id, Person person) {

    Person existingPerson =
        personOutPort.findById(id).orElseThrow(() -> EntityNotFoundException.forPerson(id));

    if (!existingPerson.getEmail().equals(person.getEmail())) {
      if (personOutPort.existsByEmail(person.getEmail())) {
        throw BusinessRuleViolationException.emailConflict(person.getEmail());
      }
    }

    existingPerson.setFullName(person.getFullName());
    existingPerson.setEmail(person.getEmail());
    existingPerson.setPhone(person.getPhone());

    return personOutPort
        .update(existingPerson)
        .orElseThrow(() -> EntityNotFoundException.forPerson(id));
  }
}
