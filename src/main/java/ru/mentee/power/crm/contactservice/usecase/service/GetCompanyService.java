package ru.mentee.power.crm.contactservice.usecase.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.mentee.power.crm.contactservice.domain.exception.EntityNotFoundException;
import ru.mentee.power.crm.contactservice.domain.exception.ValidationException;
import ru.mentee.power.crm.contactservice.domain.model.Company;
import ru.mentee.power.crm.contactservice.usecase.port.in.GetCompanyUseCase;
import ru.mentee.power.crm.contactservice.usecase.port.out.CompanyOutPort;

@Service
@RequiredArgsConstructor
public class GetCompanyService implements GetCompanyUseCase {
  private final CompanyOutPort companyOutPort;

  @Override
  public Page<Company> findByNamePageable(String name, Pageable pageable) {
    if (pageable.getPageSize() >= 100) {
      throw new ValidationException("Size must be less than or equal to 100");
    }
    return companyOutPort.findByNamePageAble(name, pageable);
  }

  @Override
  public Company getById(UUID id) {
    return companyOutPort.findById(id).orElseThrow(() -> EntityNotFoundException.forCompany(id));
  }

  @Override
  public Company findById(UUID id) {
    return companyOutPort.findById(id).orElseThrow(() -> EntityNotFoundException.forCompany(id));
  }
}
