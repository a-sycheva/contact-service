package ru.mentee.power.crm.contactservice.adapter.out.persistence.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.mentee.power.crm.contactservice.adapter.out.persistence.entity.CompanyEntity;

public interface CompanyJpaRepository extends JpaRepository<CompanyEntity, UUID> {
  Optional<CompanyEntity> findByInn(String inn);

  boolean existsByInn(String inn);

  Page<CompanyEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
