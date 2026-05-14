package ru.mentee.power.crm.contactservice.adapter.in.rest;

import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import ru.mentee.power.crm.contactservice.adapter.out.persistence.entity.CompanyEntity;
import ru.mentee.power.crm.contactservice.adapter.out.persistence.repository.CompanyJpaRepository;

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

  @BeforeEach
  void cleanUp() {
    companyRepository.deleteAll();
  }

  @Test
  void createCompanyShouldReturnCreatedWhenDataIsValid() throws Exception {

    UUID id = UUID.randomUUID();
    String json =
        """
        {
          "name": "Test Corp",
          "inn": "000000000000",
          "personId": "%s",
          "role": "Test Role"
        }
        """
            .formatted(id);

    mockMvc
        .perform(post("/api/v1/companies").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isCreated())
        .andExpect(header().string("location", matchesPattern("/api/v1/companies/.+")))
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.name").value("Test Corp"))
        .andExpect(jsonPath("$.persons").isEmpty());
  }

  @Test
  void createCompanyShouldReturnConflictWhenDuplicateInn() throws Exception {
    CompanyEntity company = createCompanyEntity("Test Corp", "000000000000");
    companyRepository.save(company);

    UUID id = UUID.randomUUID();
    String json =
        """
        {
          "name": "Another Test Corp",
          "inn": "000000000000",
          "personId": "%s",
          "role": "Test Role"
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
          "role": "Test Role"
        }
        """
            .formatted(id);

    mockMvc
        .perform(post("/api/v1/companies").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
  }

  CompanyEntity createCompanyEntity(String name, String inn) {
    return new CompanyEntity(
        UUID.randomUUID(), name, inn, LocalDateTime.now(), LocalDateTime.now());
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
  void getCompanyByIdShouldReturnNotfoundWhenCompanyNotExists() throws Exception {

    mockMvc
        .perform(get("/api/v1/companies/" + UUID.randomUUID()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errorCode").value("COMPANY_NOT_FOUND"));
  }
}
