package ru.mentee.power.crm.contactservice.usecase.service;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.contactservice.domain.exception.BusinessRuleViolationException;
import ru.mentee.power.crm.contactservice.domain.exception.EntityNotFoundException;
import ru.mentee.power.crm.contactservice.domain.model.Company;
import ru.mentee.power.crm.contactservice.usecase.port.in.UpdateCompanyUseCase;
import ru.mentee.power.crm.contactservice.usecase.port.out.CompanyOutPort;

@Service
@RequiredArgsConstructor
public class UpdateCompanyService implements UpdateCompanyUseCase {
  private final CompanyOutPort companyOutPort;

  @Override
  @Transactional
  public Company updateCompany(UUID id, Company company) {
    Company existingCompany =
        companyOutPort.findById(id).orElseThrow(() -> EntityNotFoundException.forCompany(id));

    if (!existingCompany.getInn().equals(company.getInn())) {
      if (companyOutPort.existsByInn(company.getInn())) {
        throw BusinessRuleViolationException.innConflict(company.getInn());
      }
    }

    existingCompany.setName(company.getName());
    existingCompany.setInn(company.getInn());
    existingCompany.setUpdatedAt(LocalDateTime.now());

    return companyOutPort.update(existingCompany);
  }
}
