package ru.mentee.power.crm.contactservice.usecase.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.contactservice.domain.exception.BusinessRuleViolationException;
import ru.mentee.power.crm.contactservice.domain.exception.EntityNotFoundException;
import ru.mentee.power.crm.contactservice.domain.model.Company;
import ru.mentee.power.crm.contactservice.usecase.port.in.CreateCompanyUseCase;
import ru.mentee.power.crm.contactservice.usecase.port.out.CompanyOutPort;
import ru.mentee.power.crm.contactservice.usecase.port.out.PersonCompanyLinkOutPort;
import ru.mentee.power.crm.contactservice.usecase.port.out.PersonOutPort;

@Service
@RequiredArgsConstructor
public class CreateCompanyService implements CreateCompanyUseCase {
  private final CompanyOutPort companyOutPort;
  private final PersonOutPort personOutPort;
  private final PersonCompanyLinkOutPort linkOutPort;

  @Override
  @Transactional
  public Company create(Company company, UUID personId, String role, String title) {

    if (!personOutPort.existsById(personId)) {
      throw EntityNotFoundException.forPerson(personId);
    }

    if (companyOutPort.existsByInn(company.getInn())) {
      throw BusinessRuleViolationException.innConflict(company.getInn());
    }

    Company createdCompany = companyOutPort.save(company);
    linkOutPort.createLink(personId, createdCompany.getId(), role, title);
    return createdCompany;
  }
}
