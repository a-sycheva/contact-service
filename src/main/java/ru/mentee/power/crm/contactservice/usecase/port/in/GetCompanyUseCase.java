package ru.mentee.power.crm.contactservice.usecase.port.in;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.mentee.power.crm.contactservice.domain.model.Company;

public interface GetCompanyUseCase {
  Page<Company> findByNamePageable(String name, Pageable pageable);

  Company getById(UUID id);
}
