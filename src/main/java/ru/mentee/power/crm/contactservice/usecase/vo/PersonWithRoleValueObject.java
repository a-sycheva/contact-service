package ru.mentee.power.crm.contactservice.usecase.vo;

import java.util.UUID;

public record PersonWithRoleValueObject(
    UUID id, String fullName, String email, String phone, String role, String title) {}
