package ru.mentee.power.crm.contactservice.usecase.service;

import java.security.SecureRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mentee.power.crm.contactservice.domain.exception.BusinessRuleViolationException;
import ru.mentee.power.crm.contactservice.domain.exception.EntityNotFoundException;
import ru.mentee.power.crm.contactservice.domain.exception.ValidationException;
import ru.mentee.power.crm.contactservice.domain.model.Invite;
import ru.mentee.power.crm.contactservice.domain.model.InviteStatus;
import ru.mentee.power.crm.contactservice.usecase.port.in.InviteUseCase;
import ru.mentee.power.crm.contactservice.usecase.port.out.CompanyOutPort;
import ru.mentee.power.crm.contactservice.usecase.port.out.InviteOutPort;

@Service
@RequiredArgsConstructor
public class InviteService implements InviteUseCase {
  private final InviteOutPort inviteOutPort;
  private final CompanyOutPort companyOutPort;
  private static final String CHARACTERS =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  private static final SecureRandom RANDOM = new SecureRandom();

  @Override
  public Invite create(Invite invite, long expiresInHours) {
    if (expiresInHours <= 0 || expiresInHours > 168) {
      throw new ValidationException("expiresInHours must be between" + 0 + "and" + 168);
    }
    if (!companyOutPort.existsById(invite.getCompanyId())) {
      throw EntityNotFoundException.forCompany(invite.getCompanyId());
    }
    if (inviteOutPort.existsInviteWithStatus(
        invite.getEmail(), invite.getCompanyId(), InviteStatus.PENDING)) {
      throw BusinessRuleViolationException.inviteConflict(invite.getEmail(), invite.getCompanyId());
    }

    invite.setExpiresAt(invite.getCreatedAt().plusHours(expiresInHours));
    invite.setReferralCode(generateUniqueReferralCode());

    return inviteOutPort.createInvite(invite);
  }

  private String generateUniqueReferralCode() {
    String code;
    do {
      code = generateReferralCode();
    } while (inviteOutPort.existsByReferralCode(code));
    return code;
  }

  private static String generateReferralCode() {
    StringBuilder sb = new StringBuilder(8);
    for (int i = 0; i < 8; i++) {
      sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
    }
    return sb.toString();
  }
}
