package ru.mentee.power.crm.contactservice.usecase.port.in;

import ru.mentee.power.crm.contactservice.domain.model.Person;

public interface CreatePersonUseCase {
  Person create(Person person);
}
