package ru.mentee.power.crm.contactservice.usecase.port.in;

import ru.mentee.power.crm.contactservice.domain.model.Invite;

public interface InviteUseCase {
  Invite create(Invite invite, long expiresInHours);
}
