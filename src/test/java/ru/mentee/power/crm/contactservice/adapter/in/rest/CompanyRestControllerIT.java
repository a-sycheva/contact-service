package ru.mentee.power.crm.contactservice.adapter.in.rest;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
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
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.mentee.power.crm.contactservice.adapter.out.persistence.entity.CompanyEntity;
import ru.mentee.power.crm.contactservice.adapter.out.persistence.entity.PersonEntity;
import ru.mentee.power.crm.contactservice.adapter.out.persistence.repository.CompanyJpaRepository;
import ru.mentee.power.crm.contactservice.adapter.out.persistence.repository.LinkJpaRepository;
import ru.mentee.power.crm.contactservice.adapter.out.persistence.repository.PersonJpaRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
public class CompanyRestControllerIT {

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

  @Autowired private CompanyJpaRepository companyRepository;

  @Autowired private PersonJpaRepository personRepository;

  @Autowired private LinkJpaRepository linkRepository;

  @BeforeEach
  void cleanUp() {
    linkRepository.deleteAll();
    companyRepository.deleteAll();
    personRepository.deleteAll();
  }

  @Test
  void createCompanyShouldReturnCreatedWhenDataIsValid() throws Exception {
    PersonEntity person = createPersonEntity("Test User", "test@example.com", "123456789");
    personRepository.save(person);

    UUID id = person.getId();
    String json =
        """
        {
          "name": "Test Corp",
          "inn": "000000000000",
          "personId": "%s",
          "role": "EMPLOYEE",
          "title": "IT-engineer"
        }
        """
            .formatted(id);

    mockMvc
        .perform(post("/api/v1/companies").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isCreated())
        .andExpect(header().string("location", matchesPattern("/api/v1/companies/.+")))
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.name").value("Test Corp"))
        .andExpect(jsonPath("$.persons").exists())
        .andExpect(jsonPath("$.persons[0].id").value(id.toString()))
        .andExpect(jsonPath("$.persons[0].fullName").value(person.getFullName()))
        .andExpect(jsonPath("$.persons[0].email").value(person.getEmail()))
        .andExpect(jsonPath("$.persons[0].phone").value(person.getPhone()))
        .andExpect(jsonPath("$.persons[0].role").value("EMPLOYEE"))
        .andExpect(jsonPath("$.persons[0].title").value("IT-engineer"));
  }

  @Test
  void createCompanyShouldReturnConflictWhenDuplicateInn() throws Exception {
    PersonEntity person = createPersonEntity("Test User", "test@example.com", "123456789");
    personRepository.save(person);

    CompanyEntity company = createCompanyEntity("Test Corp", "000000000000");
    companyRepository.save(company);

    UUID id = person.getId();
    String json =
        """
        {
          "name": "Another Test Corp",
          "inn": "000000000000",
          "personId": "%s",
          "role": "EMPLOYEE"
        }
        """
            .formatted(id);

    mockMvc
        .perform(post("/api/v1/companies").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.errorCode").value("COMPANY_INN_CONFLICT"));
  }

  @Test
  void createCompanyShouldReturnBadRequestWhenDataIsInvalid() throws Exception {
    UUID id = UUID.randomUUID();
    String json =
        """
        {
          "name": "Another Test Corp",
          "inn": "000",
          "personId": "%s",
          "role": "EMPLOYEE"
        }
        """
            .formatted(id);

    mockMvc
        .perform(post("/api/v1/companies").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
  }

  @Test
  void createCompanyShouldReturnNotFoundWhenPersonNotExists() throws Exception {
    UUID id = UUID.randomUUID();
    String json =
        """
        {
          "name": "Test Corp",
          "inn": "000000000000",
          "personId": "%s",
          "role": "EMPLOYEE",
          "title": "IT-engineer"
        }
        """
            .formatted(id);

    mockMvc
        .perform(post("/api/v1/companies").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errorCode").value("PERSON_NOT_FOUND"));
  }

  CompanyEntity createCompanyEntity(String name, String inn) {
    return new CompanyEntity(
        UUID.randomUUID(), name, inn, LocalDateTime.now(), LocalDateTime.now(), null);
  }

  @Test
  void listCompaniesShouldReturnOkWhenFilteredByName() throws Exception {
    CompanyEntity company = createCompanyEntity("Test Corp", "000000000000");
    companyRepository.save(company);
    CompanyEntity anotherCompany = createCompanyEntity("Another Corp", "111111111111");
    companyRepository.save(anotherCompany);

    mockMvc
        .perform(get("/api/v1/companies").param("name", "Test"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.content[0].name").value("Test Corp"));
  }

  @Test
  void listCompaniesShouldReturnOkWithoutFilter() throws Exception {
    CompanyEntity company = createCompanyEntity("Test Corp", "000000000000");
    companyRepository.save(company);
    CompanyEntity anotherCompany = createCompanyEntity("Another Corp", "111111111111");
    companyRepository.save(anotherCompany);

    mockMvc
        .perform(get("/api/v1/companies"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.content[0].name").value("Test Corp"))
        .andExpect(jsonPath("$.content[1].name").value("Another Corp"));
  }

  @Test
  void listCompaniesShouldReturnBadRequestWhenSizeTooBig() throws Exception {
    mockMvc
        .perform(get("/api/v1/companies").param("size", "200"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
  }

  @Test
  void listCompaniesShouldUseDefaultProperties() throws Exception {
    mockMvc
        .perform(get("/api/v1/companies"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.page").value(0))
        .andExpect(jsonPath("$.size").value(20));
  }

  @Test
  void getCompanyByIdShouldReturnOkWhenCompanyExists() throws Exception {
    CompanyEntity company = createCompanyEntity("Test Corp", "000000000000");
    companyRepository.save(company);

    mockMvc
        .perform(get("/api/v1/companies/" + company.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Test Corp"));
  }

  @Test
  void getCompanyByIdShouldReturnNotFoundWhenCompanyNotExists() throws Exception {

    mockMvc
        .perform(get("/api/v1/companies/" + UUID.randomUUID()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errorCode").value("COMPANY_NOT_FOUND"));
  }

  PersonEntity createPersonEntity(String fullname, String email, String phone) {
    return new PersonEntity(
        UUID.randomUUID(), fullname, email, phone, LocalDateTime.now(), LocalDateTime.now(), null);
  }

  @Test
  void deleteCompanyShouldReturnNoContentWhenSuccess() throws Exception {
    CompanyEntity company = createCompanyEntity("Test Corp", "000000000000");
    companyRepository.save(company);

    mockMvc
        .perform(delete("/api/v1/companies/" + company.getId()))
        .andExpect(status().isNoContent());
  }

  @Test
  void deleteCompanyShouldReturnNotFoundWhenCompanyNotExists() throws Exception {
    mockMvc
        .perform(delete("/api/v1/companies/" + UUID.randomUUID()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errorCode").value("COMPANY_NOT_FOUND"));
  }

  @Test
  void deleteCompanyShouldReturnConflictWhenCompanyHasLinks() throws Exception {
    PersonEntity person = createPersonEntity("Test User", "test@example.com", "123456789");
    personRepository.save(person);

    UUID id = person.getId();
    String json =
        """
        {
          "name": "Test Corp",
          "inn": "000000000000",
          "personId": "%s",
          "role": "EMPLOYEE",
          "title": "IT-engineer"
        }
        """
            .formatted(id);

    MvcResult result =
        mockMvc
            .perform(
                post("/api/v1/companies").contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isCreated())
            .andReturn();

    String responseJson = result.getResponse().getContentAsString();
    String createdCompanyId = JsonPath.parse(responseJson).read("$.id");

    mockMvc
        .perform(delete("/api/v1/companies/" + createdCompanyId))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.errorCode").value("COMPANY_HAS_LINKS"));
  }

  @Test
  void updateCompanyShouldReturnOkWhenSuccess() throws Exception {
    CompanyEntity company = createCompanyEntity("TestCorp", "000000000000");
    companyRepository.save(company);

    String json =
        """
        {
          "name": "AnotherTestCorp",
          "inn": "000000000000"
        }
        """;

    mockMvc
        .perform(
            put("/api/v1/companies/" + company.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .param("id", company.getId().toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("AnotherTestCorp"))
        .andExpect(jsonPath("$.updatedAt").value(not(equalTo(company.getUpdatedAt()))));
  }

  @Test
  void updateCompanyShouldReturnNotFoundWhenCompanyNotExists() throws Exception {
    UUID id = UUID.randomUUID();

    String json =
        """
        {
          "name": "AnotherTestCorp",
          "inn": "000000000000"
        }
        """;

    mockMvc
        .perform(
            put("/api/v1/companies/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .param("id", id.toString()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errorCode").value("COMPANY_NOT_FOUND"));
  }

  @Test
  void updateCompanyShouldReturnConflictWhenChangeInnToExisted() throws Exception {
    CompanyEntity company = createCompanyEntity("TestCorp", "000000000000");
    companyRepository.save(company);

    CompanyEntity anotherCompany = createCompanyEntity("TestIT", "111111111111");
    companyRepository.save(anotherCompany);

    String json =
        """
        {
          "name": "TestCorp",
          "inn": "111111111111"
        }
        """;

    mockMvc
        .perform(
            put("/api/v1/companies/" + company.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .param("id", company.getId().toString()))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.errorCode").value("COMPANY_INN_CONFLICT"));
  }

  @Test
  void updateCompanyShouldReturnBadRequestWhenDataIsInvalid() throws Exception {
    CompanyEntity company = createCompanyEntity("TestCorp", "000000000000");
    companyRepository.save(company);

    String json =
        """
        {
          "name": "AnotherTestCorp",
          "inn": "0000"
        }
        """;

    mockMvc
        .perform(
            put("/api/v1/companies/" + company.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .param("id", company.getId().toString()))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
  }
}
