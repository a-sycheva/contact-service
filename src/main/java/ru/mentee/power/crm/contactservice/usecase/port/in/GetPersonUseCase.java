package ru.mentee.power.crm.contactservice.usecase.port.in;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.mentee.power.crm.contactservice.domain.model.Person;

public interface GetPersonUseCase {
  Person getById(UUID id);

  Optional<Person> findByEmail(String email);

  Page<Person> findByEmailPageable(String email, Pageable pageable);
}
