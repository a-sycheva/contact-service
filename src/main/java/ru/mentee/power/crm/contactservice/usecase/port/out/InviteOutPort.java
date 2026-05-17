package ru.mentee.power.crm.contactservice.usecase.port.out;

import java.util.UUID;
import ru.mentee.power.crm.contactservice.domain.model.Invite;
import ru.mentee.power.crm.contactservice.domain.model.InviteStatus;

public interface InviteOutPort {
  boolean existsInviteWithStatus(String email, UUID companyId, InviteStatus status);

  Invite createInvite(Invite invite);
}
