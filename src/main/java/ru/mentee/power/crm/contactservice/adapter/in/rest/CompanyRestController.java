package ru.mentee.power.crm.contactservice.adapter.in.rest;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.mentee.power.crm.contactservice.adapter.in.rest.api.CompaniesApi;
import ru.mentee.power.crm.contactservice.adapter.in.rest.dto.CompanyResponse;
import ru.mentee.power.crm.contactservice.adapter.in.rest.dto.CreateCompanyRequest;
import ru.mentee.power.crm.contactservice.adapter.in.rest.dto.ListCompanies200Response;
import ru.mentee.power.crm.contactservice.adapter.in.rest.dto.PersonWithRole;
import ru.mentee.power.crm.contactservice.adapter.in.rest.dto.UpdateCompanyRequest;
import ru.mentee.power.crm.contactservice.adapter.in.rest.mapper.CompanyRestMapper;
import ru.mentee.power.crm.contactservice.adapter.in.rest.mapper.PersonWithRoleMapper;
import ru.mentee.power.crm.contactservice.domain.model.Company;
import ru.mentee.power.crm.contactservice.usecase.port.in.CreateCompanyUseCase;
import ru.mentee.power.crm.contactservice.usecase.port.in.DeleteCompanyUseCase;
import ru.mentee.power.crm.contactservice.usecase.port.in.GetCompanyUseCase;
import ru.mentee.power.crm.contactservice.usecase.port.in.PersonCompanyLinkUseCase;
import ru.mentee.power.crm.contactservice.usecase.port.in.UpdateCompanyUseCase;

@RestController
@RequiredArgsConstructor
public class CompanyRestController implements CompaniesApi {
  public final CreateCompanyUseCase createCompanyUseCase;
  public final GetCompanyUseCase getCompanyUseCase;
  public final DeleteCompanyUseCase deleteCompanyUseCase;
  public final UpdateCompanyUseCase updateCompanyUseCase;
  public final PersonCompanyLinkUseCase personCompanyLinkUseCase;
  public final CompanyRestMapper mapper;
  public final PersonWithRoleMapper personWithRoleMapper;

  @Override
  public ResponseEntity<CompanyResponse> createCompany(CreateCompanyRequest request) {
    Company company = mapper.toDomain(request);
    Company cretedCompany =
        createCompanyUseCase.create(
            company, request.getPersonId(), request.getRole().toString(), request.getTitle());

    CompanyResponse response = mapper.toResponse(cretedCompany);
    List<PersonWithRole> personList =
        personCompanyLinkUseCase.getPersonsWithRolesByCompanyId(cretedCompany.getId()).stream()
            .map(personWithRoleMapper::toDataTransferObject)
            .collect(Collectors.toList());
    response.setPersons(personList);

    URI uri = URI.create("/api/v1/companies/" + cretedCompany.getId());
    return ResponseEntity.created(uri).body(response);
  }

  @Override
  public ResponseEntity<Void> deleteCompany(UUID id) {
    deleteCompanyUseCase.deleteCompany(id);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<ListCompanies200Response> listCompanies(
      String name, Integer page, Integer size) {
    int pageNum = 0;
    if (page != null) {
      pageNum = page;
    }
    int pageSize = 20;
    if (size != null) {
      pageSize = size;
    }
    PageRequest pageable = PageRequest.of(pageNum, pageSize);

    Page<Company> companyPage = getCompanyUseCase.findByNamePageable(name, pageable);

    ListCompanies200Response response = new ListCompanies200Response();
    response.setPage(pageNum);
    response.setSize(pageSize);
    response.setTotalElements(companyPage.getTotalElements());
    response.setTotalPages(companyPage.getTotalPages());

    List<CompanyResponse> content =
        companyPage.getContent().stream().map(mapper::toResponse).collect(Collectors.toList());

    response.setContent(content);

    return ResponseEntity.ok().body(response);
  }

  @Override
  public ResponseEntity<CompanyResponse> updateCompany(
      UUID id, UpdateCompanyRequest updateCompanyRequest) {
    Company company = getCompanyUseCase.getById(id);
    mapper.updateEntity(updateCompanyRequest, company);
    Company updatedCompany = updateCompanyUseCase.updateCompany(id, company);
    return ResponseEntity.ok().body(mapper.toResponse(updatedCompany));
  }

  @Override
  public ResponseEntity<CompanyResponse> getCompanyById(UUID id) {
    Company company = getCompanyUseCase.getById(id);
    CompanyResponse response = mapper.toResponse(company);
    return ResponseEntity.ok().body(response);
  }
}
