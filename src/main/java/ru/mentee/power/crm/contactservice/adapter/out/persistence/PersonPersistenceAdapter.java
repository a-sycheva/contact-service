package ru.mentee.power.crm.contactservice.adapter.out.persistence;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.mentee.power.crm.contactservice.adapter.out.persistence.entity.PersonEntity;
import ru.mentee.power.crm.contactservice.adapter.out.persistence.mapper.PersonPersistenceMapper;
import ru.mentee.power.crm.contactservice.adapter.out.persistence.repository.PersonJpaRepository;
import ru.mentee.power.crm.contactservice.domain.model.Person;
import ru.mentee.power.crm.contactservice.usecase.port.out.PersonOutPort;

@Repository
@RequiredArgsConstructor
public class PersonPersistenceAdapter implements PersonOutPort {
  private final PersonJpaRepository repository;
  private final PersonPersistenceMapper mapper;

  @Override
  public Person save(Person person) {
    PersonEntity saved = repository.save(mapper.toEntity(person));
    return mapper.toDomain(saved);
  }

  @Override
  public boolean delete(UUID id) {
    if (repository.existsById(id)) {
      repository.deleteById(id);
      return true;
    }
    return false;
  }

  @Override
  public Optional<Person> update(Person person) {
    return repository
        .findById(person.getId())
        .map(
            existingEntity -> {
              existingEntity.setFullName(person.getFullName());
              existingEntity.setEmail(person.getEmail());
              existingEntity.setPhone(person.getPhone());
              return mapper.toDomain(repository.save(existingEntity));
            });
  }

  @Override
  public Optional<Person> findByEmail(String email) {
    return repository.findByEmail(email).map(mapper::toDomain);
  }

  @Override
  public Optional<Person> findById(UUID id) {
    return repository.findById(id).map(mapper::toDomain);
  }

  @Override
  public boolean existsByEmail(String email) {
    return repository.existsByEmail(email);
  }

  @Override
  public boolean existsById(UUID id) {
    return repository.existsById(id);
  }
}
