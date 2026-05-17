package ru.mentee.power.crm.contactservice.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class Invite {
  private UUID id;
  private String email;
  private UUID companyId;
  private PersonRole role;
  private InviteStatus status;
  private String referralCode;
  private UUID inviterPersonId;
  private LocalDateTime createdAt;
  private LocalDateTime expiresAt;

  public Invite() {
    this.id = UUID.randomUUID();
    this.status = InviteStatus.PENDING;
    this.createdAt = LocalDateTime.now();
  }
}
