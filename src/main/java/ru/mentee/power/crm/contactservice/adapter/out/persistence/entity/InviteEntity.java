package ru.mentee.power.crm.contactservice.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.mentee.power.crm.contactservice.domain.model.InviteStatus;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "invites")
public class InviteEntity {
  @Id private UUID id;

  @Column(nullable = false)
  private String email;

  @Column(name = "company_id", nullable = false)
  private UUID companyId;

  @Column(nullable = false)
  private String role;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private InviteStatus status;

  @Column(name = "referral_code", nullable = false)
  private String referralCode;

  @Column(name = "inviter_person_id")
  private UUID inviterPersonId;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "expires_at", nullable = false)
  private LocalDateTime expiresAt;
}
