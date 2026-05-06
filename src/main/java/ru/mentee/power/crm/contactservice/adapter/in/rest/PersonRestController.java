package ru.mentee.power.crm.contactservice.adapter.in.rest;

import java.net.URI;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.mentee.power.crm.contactservice.adapter.in.rest.api.PersonsApi;
import ru.mentee.power.crm.contactservice.adapter.in.rest.dto.CreatePersonRequest;
import ru.mentee.power.crm.contactservice.adapter.in.rest.dto.PersonResponse;
import ru.mentee.power.crm.contactservice.adapter.in.rest.mapper.PersonRestMapper;
import ru.mentee.power.crm.contactservice.domain.model.Person;
import ru.mentee.power.crm.contactservice.usecase.port.in.CreatePersonUseCase;
import ru.mentee.power.crm.contactservice.usecase.port.in.GetPersonUseCase;

@RestController
@RequiredArgsConstructor
public class PersonRestController implements PersonsApi {
  private final CreatePersonUseCase createPersonUseCase;
  private final GetPersonUseCase getPersonUseCase;
  private final PersonRestMapper mapper;

  @Override
  public ResponseEntity<PersonResponse> createPerson(CreatePersonRequest request) {
    Person createdPerson = createPersonUseCase.create(mapper.toDomain(request));
    PersonResponse response = mapper.toResponse(createdPerson);
    URI uri = URI.create("/api/v1/persons/" + response.getId());
    return ResponseEntity.created(uri).body(response);
  }

  @Override
  public ResponseEntity<PersonResponse> getPersonById(UUID id) {
    Person person = getPersonUseCase.getById(id);
    return ResponseEntity.ok(mapper.toResponse(person));
  }
}
