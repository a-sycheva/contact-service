--liquibase formatted sql
--changeset Anastasiya:MCRM-21-1

CREATE TABLE persons (
    id UUID PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(50) NULL,
    created_at	TIMESTAMPTZ	NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_persons_created_at ON persons(created_at DESC)