package ru.mentee.power.crm.contactservice.usecase.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.contactservice.domain.exception.BusinessRuleViolationException;
import ru.mentee.power.crm.contactservice.domain.model.Company;
import ru.mentee.power.crm.contactservice.usecase.port.in.CreateCompanyUseCase;
import ru.mentee.power.crm.contactservice.usecase.port.out.CompanyOutPort;
import ru.mentee.power.crm.contactservice.usecase.port.out.PersonOutPort;

@Service
@RequiredArgsConstructor
public class CreateCompanyService implements CreateCompanyUseCase {
  private final CompanyOutPort companyOutPort;
  private final PersonOutPort personOutPort;

  @Override
  @Transactional
  public Company create(Company company, UUID id, String role) {

    //    TODO: потребуется после добавления связи с Person
    //    if(!personOutPort.existsById(id))
    //    {
    //      throw EntityNotFoundException.forPerson(id);
    //    }

    if (companyOutPort.existsByInn(company.getInn())) {
      throw BusinessRuleViolationException.innConflict(company.getInn());
    }
    return companyOutPort.save(company);
  }
}
