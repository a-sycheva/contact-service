package ru.mentee.power.crm.contactservice.adapter.out.persistence.mapper;

import org.mapstruct.Mapper;
import ru.mentee.power.crm.contactservice.adapter.out.persistence.entity.InviteEntity;
import ru.mentee.power.crm.contactservice.domain.model.Invite;

@Mapper(componentModel = "spring")
public interface InvitePersistenceMapper {

  Invite toDomain(InviteEntity entity);

  InviteEntity toEntity(Invite domain);
}
