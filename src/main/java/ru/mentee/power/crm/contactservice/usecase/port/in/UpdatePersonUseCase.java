package ru.mentee.power.crm.contactservice.usecase.port.in;

import java.util.UUID;
import ru.mentee.power.crm.contactservice.domain.model.Person;

public interface UpdatePersonUseCase {
  Person updatePerson(UUID id, Person updatedPerson);
}
