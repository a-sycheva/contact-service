package ru.mentee.power.crm.contactservice.adapter.out.persistence.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mentee.power.crm.contactservice.adapter.out.persistence.entity.InviteEntity;
import ru.mentee.power.crm.contactservice.domain.model.InviteStatus;

@Repository
public interface InviteJpaRepository extends JpaRepository<InviteEntity, UUID> {
  boolean existsByEmailAndCompanyIdAndStatus(String email, UUID companyId, InviteStatus status);
}
