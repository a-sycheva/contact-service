package ru.mentee.power.crm.contactservice.adapter.in.rest.mapper;

import org.mapstruct.Mapper;
import ru.mentee.power.crm.contactservice.adapter.in.rest.dto.PersonWithRole;
import ru.mentee.power.crm.contactservice.usecase.vo.PersonWithRoleValueObject;

@Mapper(componentModel = "spring")
public interface PersonWithRoleMapper {

  PersonWithRoleValueObject toValueObject(PersonWithRole dto);

  PersonWithRole toDataTransferObject(PersonWithRoleValueObject vo);
}
