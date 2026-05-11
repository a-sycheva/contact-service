package ru.mentee.power.crm.contactservice.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class Company {
  private UUID id;
  private String name;
  private String inn;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public Company() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
    this.id = UUID.randomUUID();
  }
}
