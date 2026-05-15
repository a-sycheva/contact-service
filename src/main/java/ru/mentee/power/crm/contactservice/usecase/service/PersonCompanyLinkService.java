package ru.mentee.power.crm.contactservice.usecase.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.contactservice.domain.exception.BusinessRuleViolationException;
import ru.mentee.power.crm.contactservice.domain.exception.EntityNotFoundException;
import ru.mentee.power.crm.contactservice.usecase.port.in.PersonCompanyLinkUseCase;
import ru.mentee.power.crm.contactservice.usecase.port.out.CompanyOutPort;
import ru.mentee.power.crm.contactservice.usecase.port.out.PersonCompanyLinkOutPort;
import ru.mentee.power.crm.contactservice.usecase.port.out.PersonOutPort;
import ru.mentee.power.crm.contactservice.usecase.vo.PersonCompanyLinkValueObject;
import ru.mentee.power.crm.contactservice.usecase.vo.PersonWithRoleValueObject;

@Service
@RequiredArgsConstructor
public class PersonCompanyLinkService implements PersonCompanyLinkUseCase {
  private final PersonCompanyLinkOutPort linkOutPort;
  private final CompanyOutPort companyOutPort;
  private final PersonOutPort personOutPort;

  @Transactional
  @Override
  public PersonCompanyLinkValueObject createLink(PersonCompanyLinkValueObject vo) {
    if (!personOutPort.existsById(vo.personId())) {
      throw EntityNotFoundException.forPerson(vo.personId());
    }
    if (!companyOutPort.existsById(vo.companyId())) {
      throw EntityNotFoundException.forCompany(vo.companyId());
    }
    if (linkOutPort.existsByPersonIdAndCompanyId(vo.personId(), vo.companyId())) {
      throw BusinessRuleViolationException.linkConflict(vo.personId(), vo.companyId());
    }

    return linkOutPort.createLink(vo.personId(), vo.companyId(), vo.role().toString(), vo.title());
  }

  @Override
  public List<PersonWithRoleValueObject> getPersonsWithRolesByCompanyId(UUID companyId) {
    return linkOutPort.findPersonsWithRolesByCompanyId(companyId);
  }

  @Transactional
  @Override
  public void deleteLink(UUID personId, UUID companyId) {
    if (!linkOutPort.existsByPersonIdAndCompanyId(personId, companyId)) {
      throw EntityNotFoundException.forLink(personId, companyId);
    }

    linkOutPort.deleteByPersonIdAndCompanyId(personId, companyId);
  }
}
