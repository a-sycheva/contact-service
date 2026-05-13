package ru.mentee.power.crm.contactservice.adapter.out.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.mentee.power.crm.contactservice.adapter.out.persistence.entity.PersonCompanyLinkEntity;
import ru.mentee.power.crm.contactservice.usecase.vo.PersonCompanyLinkValueObject;

@Mapper(componentModel = "spring")
public interface LinkPersistenceMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "person", ignore = true)
  @Mapping(target = "company", ignore = true)
  @Mapping(target = "role", expression = "java(vo.role() != null ? vo.role().name() : null)")
  @Mapping(target = "createdAt", ignore = true)
  PersonCompanyLinkEntity toEntity(PersonCompanyLinkValueObject vo);

  // Entity → VO
  @Mapping(target = "personId", source = "person.id")
  @Mapping(target = "companyId", source = "company.id")
  @Mapping(target = "role", expression = "java(PersonRole.fromString(entity.getRole()))")
  PersonCompanyLinkValueObject toVo(PersonCompanyLinkEntity entity);
}
