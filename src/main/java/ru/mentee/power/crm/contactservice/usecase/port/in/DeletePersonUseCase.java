package ru.mentee.power.crm.contactservice.usecase.port.in;

import java.util.UUID;

public interface DeletePersonUseCase {
  void deletePerson(UUID id);
}
