package ru.mentee.power.crm.contactservice.adapter.in.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.mentee.power.crm.contactservice.adapter.in.rest.dto.CreatePersonRequest;
import ru.mentee.power.crm.contactservice.adapter.in.rest.dto.PersonResponse;
import ru.mentee.power.crm.contactservice.domain.model.Person;

@Mapper(componentModel = "spring")
public interface PersonRestMapper {
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  PersonResponse toResponse(Person person);

  @Mapping(target = "id", ignore = true)
  Person toDomain(CreatePersonRequest request);
}