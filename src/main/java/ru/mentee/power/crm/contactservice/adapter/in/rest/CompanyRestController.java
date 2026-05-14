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
import ru.mentee.power.crm.contactservice.adapter.in.rest.mapper.CompanyRestMapper;
import ru.mentee.power.crm.contactservice.domain.model.Company;
import ru.mentee.power.crm.contactservice.usecase.port.in.CreateCompanyUseCase;
import ru.mentee.power.crm.contactservice.usecase.port.in.GetCompanyUseCase;

@RestController
@RequiredArgsConstructor
public class CompanyRestController implements CompaniesApi {
  private final CreateCompanyUseCase createCompanyUseCase;
  private final GetCompanyUseCase getCompanyUseCase;
  private final CompanyRestMapper mapper;

  @Override
  public ResponseEntity<CompanyResponse> createCompany(CreateCompanyRequest request) {
    Company company = mapper.toDomain(request);
    Company createdCompany =
        createCompanyUseCase.create(company, request.getPersonId(), request.getRole());
    CompanyResponse response = mapper.toResponse(createdCompany);
    URI uri = URI.create("/api/v1/companies/" + createdCompany.getId());
    return ResponseEntity.created(uri).body(response);
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
  public ResponseEntity<CompanyResponse> getCompanyById(UUID id) {
    Company company = getCompanyUseCase.getById(id);
    CompanyResponse response = mapper.toResponse(company);
    return ResponseEntity.ok().body(response);
  }
}
