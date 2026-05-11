package ru.mentee.power.crm.contactservice.adapter.in.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.mentee.power.crm.contactservice.adapter.in.rest.dto.CompanyResponse;
import ru.mentee.power.crm.contactservice.adapter.in.rest.dto.CreateCompanyRequest;
import ru.mentee.power.crm.contactservice.domain.model.Company;

@Mapper
public interface CompanyRestMapper {

  @Mapping(target = "persons", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  CompanyResponse toResponse(Company company);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Company toDomain(CreateCompanyRequest dto);
}
