package ru.mentee.power.crm.contactservice.adapter.in.rest;

import java.net.URI;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.mentee.power.crm.contactservice.adapter.in.rest.api.LinksApi;
import ru.mentee.power.crm.contactservice.adapter.in.rest.dto.CreateLinkRequest;
import ru.mentee.power.crm.contactservice.adapter.in.rest.dto.LinkResponse;
import ru.mentee.power.crm.contactservice.adapter.in.rest.mapper.LinkRestMapper;
import ru.mentee.power.crm.contactservice.usecase.port.in.PersonCompanyLinkUseCase;
import ru.mentee.power.crm.contactservice.usecase.vo.PersonCompanyLinkValueObject;

@RestController
@RequiredArgsConstructor
public class LinkRestController implements LinksApi {
  private final PersonCompanyLinkUseCase linkUseCase;
  private final LinkRestMapper mapper;

  @Override
  public ResponseEntity<LinkResponse> createPersonCompanyLink(
      UUID id, UUID companyId, CreateLinkRequest createLinkRequest) {
    PersonCompanyLinkValueObject link =
        linkUseCase.createLink(mapper.toVo(createLinkRequest, id, companyId));
    LinkResponse response = mapper.toResponse(link);
    URI uri = URI.create("/api/v1/persons/" + id + "/companies/" + companyId);
    return ResponseEntity.created(uri).body(response);
  }

  @Override
  public ResponseEntity<Void> deletePersonCompanyLink(UUID id, UUID companyId) {
    linkUseCase.deleteLink(id, companyId);
    return ResponseEntity.noContent().build();
  }
}
