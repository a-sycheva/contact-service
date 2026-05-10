package ru.mentee.power.crm.contactservice.adapter.in.rest;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.mentee.power.crm.contactservice.adapter.in.rest.api.PersonsApi;
import ru.mentee.power.crm.contactservice.adapter.in.rest.dto.CreatePersonRequest;
import ru.mentee.power.crm.contactservice.adapter.in.rest.dto.ListPersons200Response;
import ru.mentee.power.crm.contactservice.adapter.in.rest.dto.PersonResponse;
import ru.mentee.power.crm.contactservice.adapter.in.rest.dto.UpdatePersonRequest;
import ru.mentee.power.crm.contactservice.adapter.in.rest.mapper.PersonRestMapper;
import ru.mentee.power.crm.contactservice.domain.model.Person;
import ru.mentee.power.crm.contactservice.usecase.port.in.CreatePersonUseCase;
import ru.mentee.power.crm.contactservice.usecase.port.in.DeletePersonUseCase;
import ru.mentee.power.crm.contactservice.usecase.port.in.GetPersonUseCase;
import ru.mentee.power.crm.contactservice.usecase.port.in.UpdatePersonUseCase;

@RestController
@RequiredArgsConstructor
public class PersonRestController implements PersonsApi {
  private final CreatePersonUseCase createPersonUseCase;
  private final GetPersonUseCase getPersonUseCase;
  private final DeletePersonUseCase deletePersonUseCase;
  private final UpdatePersonUseCase updatePersonUseCase;
  private final PersonRestMapper mapper;

  @Override
  public ResponseEntity<PersonResponse> createPerson(CreatePersonRequest request) {
    Person createdPerson = createPersonUseCase.create(mapper.toDomain(request));
    PersonResponse response = mapper.toResponse(createdPerson);
    URI uri = URI.create("/api/v1/persons/" + response.getId());
    return ResponseEntity.created(uri).body(response);
  }

  @Override
  public ResponseEntity<Void> deletePerson(UUID id) {
    deletePersonUseCase.deletePerson(id);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<PersonResponse> updatePerson(
      UUID id, UpdatePersonRequest updatePersonRequest) {
    Person person = getPersonUseCase.getById(id);
    mapper.updateEntity(updatePersonRequest, person);
    Person updatedPerson = updatePersonUseCase.updatePerson(id, person);
    return ResponseEntity.ok(mapper.toResponse(updatedPerson));
  }

  @Override
  public ResponseEntity<PersonResponse> getPersonById(UUID id) {
    Person person = getPersonUseCase.getById(id);
    return ResponseEntity.ok(mapper.toResponse(person));
  }

  @Override
  public ResponseEntity<ListPersons200Response> listPersons(
      String email, Integer page, Integer size) {
    int pageNum = 0;
    if (page != null) {
      pageNum = page;
    }

    int pageSize = 20;
    if (size != null) {
      pageSize = size;
    }

    Pageable pageable = PageRequest.of(pageNum, pageSize);

    Page<Person> personPage = getPersonUseCase.findByEmailPageable(email, pageable);

    ListPersons200Response response = new ListPersons200Response();
    response.setPage(personPage.getNumber());
    response.setSize(personPage.getSize());
    response.setTotalElements(personPage.getTotalElements());
    response.setTotalPages(personPage.getTotalPages());

    List<PersonResponse> content =
        personPage.getContent().stream().map(mapper::toResponse).collect(Collectors.toList());

    response.setContent(content);

    return ResponseEntity.ok(response);
  }
}
