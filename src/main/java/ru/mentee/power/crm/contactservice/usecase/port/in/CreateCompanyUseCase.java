package ru.mentee.power.crm.contactservice.usecase.port.in;

import java.util.UUID;
import ru.mentee.power.crm.contactservice.domain.model.Company;

public interface CreateCompanyUseCase {
  Company create(Company company, UUID personId, String role);
}
