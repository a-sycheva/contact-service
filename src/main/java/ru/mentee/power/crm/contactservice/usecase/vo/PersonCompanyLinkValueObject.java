package ru.mentee.power.crm.contactservice.usecase.vo;

import java.time.LocalDateTime;
import java.util.UUID;
import ru.mentee.power.crm.contactservice.domain.model.PersonRole;

public record PersonCompanyLinkValueObject(
    UUID id,
    UUID personId,
    UUID companyId,
    PersonRole role, // ← свой enum в ядре, а не в DTO!
    String title,
    LocalDateTime createdAt) {}
