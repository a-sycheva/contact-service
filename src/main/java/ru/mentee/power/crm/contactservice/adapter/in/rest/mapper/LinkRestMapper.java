package ru.mentee.power.crm.contactservice.adapter.in.rest.mapper;

import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.mentee.power.crm.contactservice.adapter.in.rest.dto.CreateLinkRequest;
import ru.mentee.power.crm.contactservice.adapter.in.rest.dto.LinkResponse;
import ru.mentee.power.crm.contactservice.domain.model.PersonRole;
import ru.mentee.power.crm.contactservice.usecase.vo.PersonCompanyLinkValueObject;

@Mapper(componentModel = "spring")
public interface LinkRestMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  PersonCompanyLinkValueObject toVo(CreateLinkRequest request, UUID personId, UUID companyId);

  LinkResponse toResponse(PersonCompanyLinkValueObject vo);

  default PersonRole toDomainRole(ru.mentee.power.crm.contactservice.adapter.in.rest.dto.PersonRole dtoRole) {
    if (dtoRole == null) return null;
    return PersonRole.valueOf(dtoRole.name());
  }

  default ru.mentee.power.crm.contactservice.adapter.in.rest.dto.PersonRole toDtoRole(PersonRole domainRole) {
    if (domainRole == null) return null;
    return ru.mentee.power.crm.contactservice.adapter.in.rest.dto.PersonRole.valueOf(domainRole.name());
  }

}
