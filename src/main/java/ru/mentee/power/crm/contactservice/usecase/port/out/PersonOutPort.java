package ru.mentee.power.crm.contactservice.usecase.port.out;

import java.util.Optional;
import java.util.UUID;
import ru.mentee.power.crm.contactservice.domain.model.Person;

public interface PersonOutPort {
  Person save(Person person);

  Optional<Person> findByEmail(String email);

  Optional<Person> findById(UUID id);

  boolean existsByEmail(String email);
}
