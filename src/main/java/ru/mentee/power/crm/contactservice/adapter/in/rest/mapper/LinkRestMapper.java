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

  default PersonRole toDomainRole(String stringRole) {
    if (stringRole == null || stringRole.isBlank()) {
      return null;
    }
    return PersonRole.valueOf(stringRole);
  }

  default String toStringRole(PersonRole domainRole) {
    if (domainRole == null) {
      return null;
    }
    return domainRole.name();
  }
}
