--liquibase formatted sql
--changeset Anastasiya:MCRM-26-1

CREATE TABLE invites (
  id UUID PRIMARY KEY,
  email VARCHAR(255) NOT NULL,
  company_id UUID NOT NULL,
  role VARCHAR(20) NOT NULL,
  status VARCHAR(20) NOT NULL,
  referral_code VARCHAR(8) NOT NULL UNIQUE,
  inviter_person_id UUID,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  expires_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_invites_email ON invites(email);

CREATE UNIQUE INDEX uq_invites_email_company_status ON invites(email, company_id, status);