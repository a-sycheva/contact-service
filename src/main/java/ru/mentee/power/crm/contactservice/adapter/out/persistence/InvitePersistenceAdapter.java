package ru.mentee.power.crm.contactservice.adapter.out.persistence;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.mentee.power.crm.contactservice.adapter.out.persistence.entity.InviteEntity;
import ru.mentee.power.crm.contactservice.adapter.out.persistence.mapper.InvitePersistenceMapper;
import ru.mentee.power.crm.contactservice.adapter.out.persistence.repository.InviteJpaRepository;
import ru.mentee.power.crm.contactservice.domain.model.Invite;
import ru.mentee.power.crm.contactservice.domain.model.InviteStatus;
import ru.mentee.power.crm.contactservice.usecase.port.out.InviteOutPort;

@Repository
@RequiredArgsConstructor
public class InvitePersistenceAdapter implements InviteOutPort {
  private final InviteJpaRepository repository;
  private final InvitePersistenceMapper mapper;

  @Override
  public boolean existsInviteWithStatus(String email, UUID companyId, InviteStatus status) {
    return repository.existsByEmailAndCompanyIdAndStatus(email, companyId, status);
  }

  @Override
  public Invite createInvite(Invite invite) {
    InviteEntity inviteEntity = mapper.toEntity(invite);
    return mapper.toDomain(repository.save(inviteEntity));
  }

  @Override
  public boolean existsByReferralCode(String referralCode) {
    return repository.existsByReferralCode(referralCode);
  }
}
