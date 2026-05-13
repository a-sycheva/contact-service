package ru.mentee.power.crm.contactservice.usecase.port.out;

import java.util.List;
import java.util.UUID;
import ru.mentee.power.crm.contactservice.usecase.vo.PersonCompanyLinkValueObject;
import ru.mentee.power.crm.contactservice.usecase.vo.PersonWithRoleValueObject;

public interface PersonCompanyLinkOutPort {

  boolean companyHasLinks(UUID companyId);

  PersonCompanyLinkValueObject createLink(UUID personId, UUID companyId, String role, String title);

  List<PersonWithRoleValueObject> findPersonsWithRolesByCompanyId(UUID companyId);

  void deleteByPersonIdAndCompanyId(UUID personId, UUID companyId);

  boolean existsByPersonIdAndCompanyId(UUID personId, UUID companyId);
}
