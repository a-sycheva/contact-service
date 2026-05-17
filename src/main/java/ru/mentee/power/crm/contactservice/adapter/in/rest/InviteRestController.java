package ru.mentee.power.crm.contactservice.adapter.in.rest;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.mentee.power.crm.contactservice.adapter.in.rest.api.InvitesApi;
import ru.mentee.power.crm.contactservice.adapter.in.rest.dto.CreateInviteRequest;
import ru.mentee.power.crm.contactservice.adapter.in.rest.dto.InviteResponse;
import ru.mentee.power.crm.contactservice.adapter.in.rest.mapper.InviteRestMapper;
import ru.mentee.power.crm.contactservice.domain.model.Invite;
import ru.mentee.power.crm.contactservice.usecase.port.in.GetCompanyUseCase;
import ru.mentee.power.crm.contactservice.usecase.port.in.InviteUseCase;

@RestController
@RequiredArgsConstructor
public class InviteRestController implements InvitesApi {
  private final InviteRestMapper mapper;
  private final InviteUseCase useCase;
  private final GetCompanyUseCase getCompanyUseCase;

  @Override
  public ResponseEntity<InviteResponse> createInvite(CreateInviteRequest createInviteRequest) {
    Invite invite = mapper.toDomain(createInviteRequest);

    InviteResponse response =
        mapper.toResponse(useCase.create(invite, createInviteRequest.getExpiresInHours()));
    URI uri = URI.create("/api/v1/invites/" + response.getId());
    response.setCompanyName(getCompanyUseCase.findById(invite.getCompanyId()).getName());
    return ResponseEntity.created(uri).body(response);
  }
}
