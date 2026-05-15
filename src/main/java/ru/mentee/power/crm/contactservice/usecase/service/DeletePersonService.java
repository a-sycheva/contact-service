package ru.mentee.power.crm.contactservice.usecase.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.contactservice.domain.exception.EntityNotFoundException;
import ru.mentee.power.crm.contactservice.usecase.port.in.DeletePersonUseCase;
import ru.mentee.power.crm.contactservice.usecase.port.out.PersonOutPort;

@Service
@RequiredArgsConstructor
public class DeletePersonService implements DeletePersonUseCase {
  private final PersonOutPort personOutPort;

  @Override
  @Transactional
  public void deletePerson(UUID id) {
    if (!personOutPort.delete(id)) {
      throw EntityNotFoundException.forPerson(id);
    }
  }
}
