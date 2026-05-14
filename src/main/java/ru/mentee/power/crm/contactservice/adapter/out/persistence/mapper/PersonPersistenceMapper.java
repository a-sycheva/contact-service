package ru.mentee.power.crm.contactservice.adapter.out.persistence.mapper;

import org.mapstruct.Mapper;
import ru.mentee.power.crm.contactservice.adapter.out.persistence.entity.PersonEntity;
import ru.mentee.power.crm.contactservice.domain.model.Person;

@Mapper(componentModel = "spring")
public interface PersonPersistenceMapper {

  PersonEntity toEntity(Person person);

  Person toDomain(PersonEntity entity);
}
