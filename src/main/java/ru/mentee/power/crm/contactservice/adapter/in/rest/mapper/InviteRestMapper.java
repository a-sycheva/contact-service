package ru.mentee.power.crm.contactservice.adapter.in.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.mentee.power.crm.contactservice.adapter.in.rest.dto.CreateInviteRequest;
import ru.mentee.power.crm.contactservice.adapter.in.rest.dto.InviteResponse;
import ru.mentee.power.crm.contactservice.domain.model.Invite;
import ru.mentee.power.crm.contactservice.domain.model.InviteStatus;

@Mapper(componentModel = "spring")
public interface InviteRestMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "referralCode", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "expiresAt", ignore = true)
  Invite toDomain(CreateInviteRequest dto);

  @Mapping(target = "companyName", ignore = true)
  InviteResponse toResponse(Invite domain);

  default InviteStatus toDomainStatus(String stringStatus) {
    if (stringStatus == null || stringStatus.isBlank()) {
      return null;
    }

    return InviteStatus.valueOf(stringStatus);
  }

  default String toStringStatus(InviteStatus domainStatus) {
    if (domainStatus == null) {
      return null;
    }
    return domainStatus.name();
  }
}
