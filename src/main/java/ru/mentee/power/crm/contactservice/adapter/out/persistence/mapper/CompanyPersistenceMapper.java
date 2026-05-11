package ru.mentee.power.crm.contactservice.adapter.out.persistence.mapper;

import org.mapstruct.Mapper;
import ru.mentee.power.crm.contactservice.adapter.out.persistence.entity.CompanyEntity;
import ru.mentee.power.crm.contactservice.domain.model.Company;

@Mapper(componentModel = "spring")
public interface CompanyPersistenceMapper {

  CompanyEntity toEntity(Company company);

  Company toDomain(CompanyEntity entity);
}
