package ru.mentee.power.crm.contactservice.usecase.vo;

import java.util.UUID;
import ru.mentee.power.crm.contactservice.domain.model.PersonRole;

public record PersonWithRoleValueObject(
    UUID id, String fullName, String email, String phone, PersonRole role, String title) {}
