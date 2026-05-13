package ru.mentee.power.crm.contactservice.adapter.out.persistence.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.mentee.power.crm.contactservice.adapter.out.persistence.entity.PersonCompanyLinkEntity;

public interface LinkJpaRepository extends JpaRepository<PersonCompanyLinkEntity, UUID> {
  boolean existsByCompanyId(UUID id);

  @EntityGraph(attributePaths = {"person"})
  List<PersonCompanyLinkEntity> findByCompanyId(UUID companyId);

  void deleteByPersonIdAndCompanyId(UUID personId, UUID companyId);

  boolean existsByPersonIdAndCompanyId(UUID personId, UUID companyId);
}
