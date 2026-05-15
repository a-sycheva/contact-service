package ru.mentee.power.crm.contactservice.adapter.in.rest.mapper;

import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.mentee.power.crm.contactservice.adapter.in.rest.dto.CreateLinkRequest;
import ru.mentee.power.crm.contactservice.adapter.in.rest.dto.LinkResponse;
import ru.mentee.power.crm.contactservice.usecase.vo.PersonCompanyLinkValueObject;

@Mapper(componentModel = "spring")
public interface LinkRestMapper {

  @Mapping(target = "role", expression = "java(PersonRole.fromString(request.getRole()))")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  PersonCompanyLinkValueObject toVo(CreateLinkRequest request, UUID personId, UUID companyId);

  @Mapping(target = "role", expression = "java(vo.role() != null ? vo.role().name() : null)")
  LinkResponse toResponse(PersonCompanyLinkValueObject vo);
}
