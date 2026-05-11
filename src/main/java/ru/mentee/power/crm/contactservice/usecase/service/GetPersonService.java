package ru.mentee.power.crm.contactservice.usecase.service;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.contactservice.domain.exception.EntityNotFoundException;
import ru.mentee.power.crm.contactservice.domain.exception.ValidationException;
import ru.mentee.power.crm.contactservice.domain.model.Person;
import ru.mentee.power.crm.contactservice.usecase.port.in.GetPersonUseCase;
import ru.mentee.power.crm.contactservice.usecase.port.out.PersonOutPort;

@Service
@RequiredArgsConstructor
public class GetPersonService implements GetPersonUseCase {
  private final PersonOutPort personOutPort;

  @Override
  @Transactional(readOnly = true)
  public Person getById(UUID id) {
    return personOutPort
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Person with id " + id + " not found"));
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Person> findByEmail(String email) {
    return personOutPort.findByEmail(email);
  }

  @Override
  public Page<Person> findByEmailPageable(String email, Pageable pageable) {
    if (pageable.getPageSize() >= 100) {
      throw new ValidationException("Size must be less than or equal to 100");
    }
    return personOutPort.findByEmailPageable(email, pageable);
  }
}
