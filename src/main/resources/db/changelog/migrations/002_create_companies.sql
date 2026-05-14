--liquibase formatted sql
--changeset Anastasiya:MCRM-24-1

CREATE TABLE companies (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    inn VARCHAR(12) NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL default now(),
    updated_at TIMESTAMPTZ NOT NULL default now()
);

CREATE INDEX idx_companies_created_at ON companies(created_at DESC);

--rollback DROP TABLE companies;