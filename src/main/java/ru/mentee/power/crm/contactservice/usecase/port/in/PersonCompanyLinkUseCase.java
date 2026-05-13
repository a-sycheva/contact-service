package ru.mentee.power.crm.contactservice.usecase.port.in;

import java.util.List;
import java.util.UUID;
import ru.mentee.power.crm.contactservice.usecase.vo.PersonCompanyLinkValueObject;
import ru.mentee.power.crm.contactservice.usecase.vo.PersonWithRoleValueObject;

public interface PersonCompanyLinkUseCase {
  PersonCompanyLinkValueObject createLink(PersonCompanyLinkValueObject vo);

  List<PersonWithRoleValueObject> getPersonsWithRolesByCompanyId(UUID companyId);

  void deleteLink(UUID id, UUID companyId);
}
