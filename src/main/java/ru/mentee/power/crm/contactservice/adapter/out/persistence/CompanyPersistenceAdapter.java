package ru.mentee.power.crm.contactservice.adapter.out.persistence;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.mentee.power.crm.contactservice.adapter.out.persistence.entity.CompanyEntity;
import ru.mentee.power.crm.contactservice.adapter.out.persistence.mapper.CompanyPersistenceMapper;
import ru.mentee.power.crm.contactservice.adapter.out.persistence.repository.CompanyJpaRepository;
import ru.mentee.power.crm.contactservice.domain.model.Company;
import ru.mentee.power.crm.contactservice.usecase.port.out.CompanyOutPort;

@Repository
@RequiredArgsConstructor
public class CompanyPersistenceAdapter implements CompanyOutPort {
  private final CompanyJpaRepository repository;
  private final CompanyPersistenceMapper mapper;

  @Override
  public Company save(Company company) {
    CompanyEntity saved = repository.save(mapper.toEntity(company));
    return mapper.toDomain(saved);
  }

  @Override
  public boolean existsByInn(String inn) {
    return repository.existsByInn(inn);
  }

  @Override
  public Optional<Company> findById(UUID id) {
    return repository.findById(id).map(mapper::toDomain);
  }

  @Override
  public Page<Company> findByNamePageAble(String name, Pageable pageable) {
    Page<CompanyEntity> entityPage;

    if (name == null || name.isBlank()) {
      entityPage = repository.findAll(pageable);
    } else {
      entityPage = repository.findByNameContainingIgnoreCase(name, pageable);
    }

    return entityPage.map(mapper::toDomain);
  }
}
