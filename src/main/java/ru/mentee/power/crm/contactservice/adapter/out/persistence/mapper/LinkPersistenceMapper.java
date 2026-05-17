package ru.mentee.power.crm.contactservice.adapter.out.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.mentee.power.crm.contactservice.adapter.out.persistence.entity.PersonCompanyLinkEntity;
import ru.mentee.power.crm.contactservice.domain.model.PersonRole;
import ru.mentee.power.crm.contactservice.usecase.vo.PersonCompanyLinkValueObject;

@Mapper(componentModel = "spring")
public interface LinkPersistenceMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "person", ignore = true)
  @Mapping(target = "company", ignore = true)
  @Mapping(target = "role", source = "role", qualifiedByName = "roleToString")
  @Mapping(target = "createdAt", ignore = true)
  PersonCompanyLinkEntity toEntity(PersonCompanyLinkValueObject vo);

  @Mapping(target = "personId", source = "person.id")
  @Mapping(target = "companyId", source = "company.id")
  @Mapping(target = "role", source = "role", qualifiedByName = "stringToRole")
  PersonCompanyLinkValueObject toVo(PersonCompanyLinkEntity entity);

  @Named("roleToString")
  default String roleToString(PersonRole role) {
    return role != null ? role.name() : null;
  }

  @Named("stringToRole")
  default PersonRole stringToRole(String role) {
    return role != null ? PersonRole.valueOf(role) : null;
  }
}
