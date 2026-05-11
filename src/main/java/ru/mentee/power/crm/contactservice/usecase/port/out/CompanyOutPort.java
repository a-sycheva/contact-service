package ru.mentee.power.crm.contactservice.usecase.port.out;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.mentee.power.crm.contactservice.domain.model.Company;

public interface CompanyOutPort {
  Company save(Company company);

  boolean existsByInn(String inn);

  Optional<Company> findById(UUID id);

  Page<Company> findByNamePageAble(String name, Pageable pageable);
}
