package ru.mentee.power.crm.contactservice.adapter.in.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.mentee.power.crm.contactservice.adapter.in.rest.dto.CreatePersonRequest;
import ru.mentee.power.crm.contactservice.adapter.in.rest.dto.PersonResponse;
import ru.mentee.power.crm.contactservice.adapter.in.rest.dto.UpdatePersonRequest;
import ru.mentee.power.crm.contactservice.domain.model.Person;

@Mapper(componentModel = "spring")
public interface PersonRestMapper {

  PersonResponse toResponse(Person person);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Person toDomain(CreatePersonRequest request);

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "id", ignore = true)
  void updateEntity(UpdatePersonRequest dto, @MappingTarget Person entity);
}
