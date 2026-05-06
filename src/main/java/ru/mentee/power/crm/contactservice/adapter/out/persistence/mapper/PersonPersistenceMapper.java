package ru.mentee.power.crm.contactservice.adapter.out.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.mentee.power.crm.contactservice.adapter.out.persistence.entity.PersonEntity;
import ru.mentee.power.crm.contactservice.domain.model.Person;

@Mapper(componentModel = "spring")
public interface PersonPersistenceMapper {

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  PersonEntity toEntity(Person person);

  Person toDomain(PersonEntity entity);
}
