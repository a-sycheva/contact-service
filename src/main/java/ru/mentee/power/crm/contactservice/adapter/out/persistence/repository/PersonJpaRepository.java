package ru.mentee.power.crm.contactservice.adapter.out.persistence.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.mentee.power.crm.contactservice.adapter.out.persistence.entity.PersonEntity;

public interface PersonJpaRepository extends JpaRepository<PersonEntity, UUID> {
  Optional<PersonEntity> findByEmail(String email);

  boolean existsByEmail(String email);

  Page<PersonEntity> findByEmailContainingIgnoreCase(String email, Pageable pageable);
}
