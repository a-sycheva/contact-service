--liquibase formatted sql
--changeset Anastasiya:MCRM-25-1

CREATE TABLE person_company_links (
    id UUID PRIMARY KEY,
    person_id UUID NOT NULL,
    company_id UUID NOT NULL,
    role VARCHAR(20) NOT NULL,
    title VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),

CONSTRAINT fk_person_company_links_person
    FOREIGN KEY (person_id) REFERENCES persons(id) ON DELETE CASCADE,
CONSTRAINT fk_person_company_links_company
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE RESTRICT
);

CREATE INDEX idx_pcl_person_id ON person_company_links(person_id);

CREATE INDEX idx_pcl_company_id ON person_company_links(company_id);

CREATE UNIQUE INDEX uq_pcl_person_company ON person_company_links(person_id, company_id);
