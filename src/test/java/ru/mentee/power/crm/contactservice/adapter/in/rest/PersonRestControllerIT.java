package ru.mentee.power.crm.contactservice.adapter.in.rest;

import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.mentee.power.crm.contactservice.adapter.out.persistence.entity.PersonEntity;
import ru.mentee.power.crm.contactservice.adapter.out.persistence.repository.PersonJpaRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
class PersonRestControllerIT {

  @Container
  static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:16")
          .withDatabaseName("testdb")
          .withUsername("test")
          .withPassword("test");

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @Autowired private MockMvc mockMvc;

  @Autowired private PersonJpaRepository personRepository;

  @BeforeEach
  void cleanUp() {
    personRepository.deleteAll();
  }

  @Test
  void createPersonShouldReturnCreatedWhenDataIsValid() throws Exception {
    String json =
        """
        {
        "fullName": "Test User",
        "email": "test@example.com",
        "phone": "123456789"
        }
        """;
    mockMvc
        .perform(post("/api/v1/persons").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isCreated())
        .andExpect(header().string("location", matchesPattern("/api/v1/persons/.+")))
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.email").value("test@example.com"))
        .andExpect(jsonPath("$.fullName").value("Test User"))
        .andExpect(jsonPath("$.phone").value("123456789"));
  }

  @Test
  void createPersonShouldReturnCreatedWhenDataIsValidWitohutPhone() throws Exception {

    String json =
        """
        {
        "fullName": "Test User",
        "email": "test@example.com",
        "phone": ""
        }
        """;
    mockMvc
        .perform(post("/api/v1/persons").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isCreated())
        .andExpect(header().string("location", matchesPattern("/api/v1/persons/.+")))
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.email").value("test@example.com"))
        .andExpect(jsonPath("$.fullName").value("Test User"))
        .andExpect(jsonPath("$.phone").value(""));
  }

  @Test
  void createPersonShouldReturnConflictWhenEmailAlreadyExists() throws Exception {

    PersonEntity person = createPersonEntity("Another User", "test@example.com", "80976543210");

    personRepository.save(person);

    String json =
        """
        {
        "fullName": "Test User",
        "email": "test@example.com",
        "phone": "123456789"
        }
        """;
    mockMvc
        .perform(post("/api/v1/persons").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.errorCode").value("PERSON_EMAIL_CONFLICT"));
  }

  @Test
  void createPersonShouldReturnBadRequestWhenEmailIsInvalid() throws Exception {
    String json =
        """
        {
        "fullName": "Test User",
        "email": "testexample.com",
        "phone": ""
        }
        """;
    mockMvc
        .perform(post("/api/v1/persons").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
  }

  @Test
  void createPersonShouldReturnBadRequestWhenNameIsNull() throws Exception {
    String json =
        """
        {
        "email": "testexample.com",
        "phone": ""
        }
        """;
    mockMvc
        .perform(post("/api/v1/persons").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
  }

  @Test
  void getPersonByIdShouldReturnOkWhenPersonExists() throws Exception {
    PersonEntity person = createPersonEntity("Test User", "test@example.com", "123456789");

    personRepository.save(person);

    mockMvc
        .perform(get("/api/v1/persons/" + person.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(person.getId().toString()))
        .andExpect(jsonPath("$.email").value(person.getEmail()))
        .andExpect(jsonPath("$.fullName").value(person.getFullName()))
        .andExpect(jsonPath("$.phone").value(person.getPhone()));
  }

  @Test
  void getPersonByIdShouldReturnNotFondWhenWhenPersonNotExists() throws Exception {
    mockMvc
        .perform(get("/api/v1/persons/" + UUID.randomUUID()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errorCode").value("PERSON_NOT_FOUND"));
  }

  @Test
  void listPersonsShouldReturnOkWhenFilteredByEmail() throws Exception {
    PersonEntity person = createPersonEntity("Test User", "test@example.com", "123456789");
    personRepository.save(person);

    PersonEntity anotherPerson =
        createPersonEntity("Another User", "another@example.com", "987654321");
    personRepository.save(anotherPerson);

    mockMvc
        .perform(
            get("/api/v1/persons")
                .param("email", "test@example.com")
                .param("page", "0")
                .param("size", "20"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.content[0].email").value("test@example.com"));
  }

  @Test
  void listPersonsShouldReturnOkWithoutFilter() throws Exception {
    PersonEntity person = createPersonEntity("Test User", "test@example.com", "123456789");
    personRepository.save(person);

    PersonEntity anotherPerson =
        createPersonEntity("Another User", "another@example.com", "987654321");
    personRepository.save(anotherPerson);

    mockMvc
        .perform(get("/api/v1/persons").param("page", "0").param("size", "20"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.content[0].email").value("test@example.com"))
        .andExpect(jsonPath("$.content[1].email").value("another@example.com"));
  }

  @Test
  void listPersonsShouldReturnBadRequestWenSizeTooBig() throws Exception {
    mockMvc
        .perform(get("/api/v1/persons").param("size", "200"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
  }

  @Test
  void listPersonsShouldPaginate() throws Exception {
    for (int i = 0; i < 10; i++) {
      personRepository.save(
          new PersonEntity(
              UUID.randomUUID(),
              "Test User" + i,
              "test" + i + "@example.ru",
              "98765432" + i,
              LocalDateTime.now(),
              LocalDateTime.now(),
              null));
    }

    mockMvc
        .perform(get("/api/v1/persons").param("page", "0").param("size", "6"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(6))
        .andExpect(jsonPath("$.page").value(0))
        .andExpect(jsonPath("$.totalPages").value(2))
        .andExpect(jsonPath("$.totalElements").value(10));

    mockMvc
        .perform(get("/api/v1/persons").param("page", "1").param("size", "6"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(4))
        .andExpect(jsonPath("$.page").value(1));
  }

  @Test
  void listPersonsShouldUseDefaultProperties() throws Exception {

    mockMvc
        .perform(get("/api/v1/persons").param("email", "test@example.com"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.page").value(0))
        .andExpect(jsonPath("$.size").value(20));
  }

  @Test
  void deletePersonShouldReturnNoContentWhenSuccess() throws Exception {
    PersonEntity person = createPersonEntity("Test User", "test@example.com", "123456789");
    personRepository.save(person);

    mockMvc.perform(delete("/api/v1/persons/" + person.getId())).andExpect(status().isNoContent());
  }

  @Test
  void deletePersonShouldReturnNotFoundWhenPersonNotExists() throws Exception {

    mockMvc
        .perform(delete("/api/v1/persons/" + UUID.randomUUID()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errorCode").value("PERSON_NOT_FOUND"));
  }

  @Test
  void updatePersonShouldReturnOkWhenSuccess() throws Exception {
    PersonEntity person = createPersonEntity("Test User", "test@example.com", "123456789");
    personRepository.save(person);

    String json =
        """
        {
        "fullName": "Another User",
        "email": "test@example.com",
        "phone" : ""
        }
        """;

    mockMvc
        .perform(
            put("/api/v1/persons/" + person.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.fullName").value("Another User"))
        .andExpect(jsonPath("$.email").value("test@example.com"))
        .andExpect(jsonPath("$.phone").value(""));
  }

  @Test
  void updatePersonShouldReturnNotFoundWhenPersonNotExists() throws Exception {
    String json =
        """
        {
        "fullName": "Another User",
        "email": "test@example.com",
        "phone" : ""
        }
        """;

    mockMvc
        .perform(
            put("/api/v1/persons/" + UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errorCode").value("PERSON_NOT_FOUND"));
  }

  @Test
  void updatePersonShouldReturnConflictWhenSetDuplicatedEmail() throws Exception {
    PersonEntity person = createPersonEntity("Test User", "test@example.com", "123456789");
    personRepository.save(person);

    PersonEntity anotherPerson =
        createPersonEntity("Another User", "another@example.com", "80987654321");
    personRepository.save(anotherPerson);

    String json =
        """
        {
        "fullName": "Test User",
        "email": "another@example.com",
        "phone" : ""
        }
        """;

    mockMvc
        .perform(
            put("/api/v1/persons/" + person.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.errorCode").value("PERSON_EMAIL_CONFLICT"));
  }

  @Test
  void updatePersonShouldReturnBadRequestWhenDataNotValid() throws Exception {
    PersonEntity person = createPersonEntity("Test User", "test@example.com", "123456789");
    personRepository.save(person);

    String json =
        """
        {
        "fullName": "Another User",
        "email": "testexample.com",
        "phone" : ""
        }
        """;

    mockMvc
        .perform(
            put("/api/v1/persons/" + person.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
  }

  PersonEntity createPersonEntity(String fullname, String email, String phone) {
    return new PersonEntity(
        UUID.randomUUID(), fullname, email, phone, LocalDateTime.now(), LocalDateTime.now(), null);
  }
}
