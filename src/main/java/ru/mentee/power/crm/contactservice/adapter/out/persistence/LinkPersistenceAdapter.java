package ru.mentee.power.crm.contactservice.adapter.out.persistence;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.mentee.power.crm.contactservice.adapter.out.persistence.entity.CompanyEntity;
import ru.mentee.power.crm.contactservice.adapter.out.persistence.entity.PersonCompanyLinkEntity;
import ru.mentee.power.crm.contactservice.adapter.out.persistence.entity.PersonEntity;
import ru.mentee.power.crm.contactservice.adapter.out.persistence.mapper.LinkPersistenceMapper;
import ru.mentee.power.crm.contactservice.adapter.out.persistence.repository.CompanyJpaRepository;
import ru.mentee.power.crm.contactservice.adapter.out.persistence.repository.LinkJpaRepository;
import ru.mentee.power.crm.contactservice.adapter.out.persistence.repository.PersonJpaRepository;
import ru.mentee.power.crm.contactservice.usecase.port.out.PersonCompanyLinkOutPort;
import ru.mentee.power.crm.contactservice.usecase.vo.PersonCompanyLinkValueObject;
import ru.mentee.power.crm.contactservice.usecase.vo.PersonWithRoleValueObject;

@Repository
@RequiredArgsConstructor
public class LinkPersistenceAdapter implements PersonCompanyLinkOutPort {
  private final LinkJpaRepository linkRepository;
  private final PersonJpaRepository personRepository;
  private final CompanyJpaRepository companyRepository;
  private final LinkPersistenceMapper mapper;

  @Override
  public boolean companyHasLinks(UUID id) {
    return linkRepository.existsByCompanyId(id);
  }

  @Override
  public PersonCompanyLinkValueObject createLink(
      UUID personId, UUID companyId, String role, String title) {
    PersonEntity person = personRepository.findById(personId).get();
    CompanyEntity company = companyRepository.findById(companyId).get();

    PersonCompanyLinkEntity link = new PersonCompanyLinkEntity();
    link.setCompany(company);
    link.setPerson(person);
    link.setTitle(title);
    link.setRole(role);

    PersonCompanyLinkValueObject vo = mapper.toVo(linkRepository.save(link));

    return vo;
  }

  @Override
  public List<PersonWithRoleValueObject> findPersonsWithRolesByCompanyId(UUID companyId) {

    return linkRepository.findByCompanyId(companyId).stream()
        .map(
            entity ->
                new PersonWithRoleValueObject(
                    entity.getPerson().getId(),
                    entity.getPerson().getFullName(),
                    entity.getPerson().getEmail(),
                    entity.getPerson().getPhone(),
                    mapper.stringToRole(entity.getRole()),
                    entity.getTitle()))
        .collect(Collectors.toList());
  }

  @Override
  public void deleteByPersonIdAndCompanyId(UUID personId, UUID companyId) {
    linkRepository.deleteByPersonIdAndCompanyId(personId, companyId);
  }

  @Override
  public boolean existsByPersonIdAndCompanyId(UUID personId, UUID companyId) {
    return linkRepository.existsByPersonIdAndCompanyId(personId, companyId);
  }
}
