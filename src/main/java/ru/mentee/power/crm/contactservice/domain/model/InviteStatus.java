package ru.mentee.power.crm.contactservice.domain.model;

import java.util.Map;
import java.util.Set;

public enum InviteStatus {
  PENDING,
  ACCEPTED,
  CANCELLED,
  EXPIRED;

  private static final Map<InviteStatus, Set<InviteStatus>> ALLOWED_TRANSITIONS =
      Map.of(
          PENDING, Set.of(ACCEPTED, CANCELLED, EXPIRED),
          ACCEPTED, Set.of(),
          CANCELLED, Set.of(),
          EXPIRED, Set.of());

  public boolean canTransitionTo(InviteStatus target) {
    return ALLOWED_TRANSITIONS.getOrDefault(this, Set.of()).contains(target);
  }
}
