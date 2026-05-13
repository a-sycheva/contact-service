package ru.mentee.power.crm.contactservice.usecase.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.contactservice.domain.exception.BusinessRuleViolationException;
import ru.mentee.power.crm.contactservice.domain.exception.EntityNotFoundException;
import ru.mentee.power.crm.contactservice.usecase.port.in.DeleteCompanyUseCase;
import ru.mentee.power.crm.contactservice.usecase.port.out.CompanyOutPort;
import ru.mentee.power.crm.contactservice.usecase.port.out.PersonCompanyLinkOutPort;

@Service
@RequiredArgsConstructor
public class DeleteCompanyService implements DeleteCompanyUseCase {
  private final CompanyOutPort companyOutPort;
  private final PersonCompanyLinkOutPort personCompanyLinkOutPort;

  @Override
  @Transactional
  public void deleteCompany(UUID id) {
    if (!companyOutPort.existsById(id)) {
      throw EntityNotFoundException.forCompany(id);
    }

    if (personCompanyLinkOutPort.companyHasLinks(id)) {
      throw BusinessRuleViolationException.activeLinks(id);
    }
    companyOutPort.delete(id);
  }
}
