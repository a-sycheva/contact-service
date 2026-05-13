package ru.mentee.power.crm.contactservice.adapter.in.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
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
import ru.mentee.power.crm.contactservice.adapter.out.persistence.repository.CompanyJpaRepository;
import ru.mentee.power.crm.contactservice.adapter.out.persistence.repository.LinkJpaRepository;
import ru.mentee.power.crm.contactservice.adapter.out.persistence.repository.PersonJpaRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
class LinkRestControllerIT {

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

  @Autowired private LinkJpaRepository linkRepository;

  @Autowired private PersonJpaRepository personRepository;

  @Autowired private CompanyJpaRepository companyRepository;

  @BeforeEach
  void cleanUp() {
    linkRepository.deleteAll();
    personRepository.deleteAll();
    companyRepository.deleteAll();
  }

  @Test
  void createPersonCompanyLinkShouldReturnCreatedWhenSuccess() throws Exception {

    String firstPersonId = addPerson("Test Person", "test@email.ru", "1234567890");
    String secondPersonId = addPerson("New Person", "new@email.ru", "0987654321");
    String companyId =
        addCompany("000000000000", "TestCorp", firstPersonId, "OWNER", "General director");

    String json =
        """
        {
          "role": "EMPLOYEE",
          "title": "Programmer"
        }
        """;

    mockMvc
        .perform(
            post("/api/v1/persons/" + secondPersonId + "/companies/" + companyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.personId").value(secondPersonId))
        .andExpect(jsonPath("$.companyId").value(companyId));
  }

  @Test
  void createPersonCompanyLinkShouldReturnBadReauestWhenDataInvalid() throws Exception {

    String firstPersonId = addPerson("Test Person", "test@email.ru", "1234567890");
    String secondPersonId = addPerson("New Person", "new@email.ru", "0987654321");
    String companyId =
        addCompany("000000000000", "TestCorp", firstPersonId, "OWNER", "General director");

    String json =
        """
        {
          "role": "another role",
          "title": "Programmer"
        }
        """;

    mockMvc
        .perform(
            post("/api/v1/persons/" + secondPersonId + "/companies/" + companyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
  }

  @Test
  void createPersonCompanyLinkShouldReturnNotFoundWhenPersonOrCompanyNotExists() throws Exception {

    String firstPersonId = addPerson("Test Person", "test@email.ru", "1234567890");
    String secondPersonId = addPerson("New Person", "new@email.ru", "0987654321");
    String companyId =
        addCompany("000000000000", "TestCorp", firstPersonId, "OWNER", "General director");

    String json =
        """
        {
          "role": "EMPLOYEE",
          "title": "Programmer"
        }
        """;

    mockMvc
        .perform(
            post("/api/v1/persons/" + UUID.randomUUID() + "/companies/" + companyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errorCode").value("PERSON_NOT_FOUND"));

    mockMvc
        .perform(
            post("/api/v1/persons/" + secondPersonId + "/companies/" + UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errorCode").value("COMPANY_NOT_FOUND"));
  }

  @Test
  void createPersonCompanyLinkShouldReturnConflictWhenLinkAlreadyExists() throws Exception {

    String firstPersonId = addPerson("Test Person", "test@email.ru", "1234567890");
    String companyId =
        addCompany("000000000000", "TestCorp", firstPersonId, "OWNER", "General director");

    String json =
        """
        {
          "role": "EMPLOYEE",
          "title": "Programmer"
        }
        """;

    mockMvc
        .perform(
            post("/api/v1/persons/" + firstPersonId + "/companies/" + companyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.errorCode").value("LINK_ALREADY_EXISTS"));
  }

  @Test
  void deletePersonCompanyLinkShouldReturnNoContentWhenSuccess() throws Exception {

    String personId = addPerson("Test Person", "test@email.ru", "1234567890");
    String companyId =
        addCompany("000000000000", "TestCorp", personId, "OWNER", "General director");

    mockMvc
        .perform(delete("/api/v1/persons/" + personId + "/companies/" + companyId))
        .andExpect(status().isNoContent());
  }

  @Test
  void deletePersonCompanyLinkShouldReturnNotFoundWhenLinkNotExists() throws Exception {
    mockMvc
        .perform(
            delete("/api/v1/persons/" + UUID.randomUUID() + "/companies/" + UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errorCode").value("LINK_NOT_FOUND"));
  }

  String addPerson(String fullName, String email, String phone) throws Exception {

    String personJson =
        """
        {
        "fullName": "%s",
        "email": "%s",
        "phone": "%s"
        }
        """
            .formatted(fullName, email, phone);

    MvcResult personResult =
        mockMvc
            .perform(
                post("/api/v1/persons").contentType(MediaType.APPLICATION_JSON).content(personJson))
            .andExpect(status().isCreated())
            .andReturn();

    String responseJson = personResult.getResponse().getContentAsString();

    return JsonPath.parse(responseJson).read("$.id");
  }

  String addCompany(String inn, String name, String personId, String role, String title)
      throws Exception {

    String companyJson =
        """
        {
          "name": "%s",
          "inn": "%s",
          "personId": "%s",
          "role": "%s",
          "title": "%s"
        }
        """
            .formatted(name, inn, personId, role, title);

    MvcResult companyResult =
        mockMvc
            .perform(
                post("/api/v1/companies")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(companyJson))
            .andExpect(status().isCreated())
            .andReturn();

    String responseJson = companyResult.getResponse().getContentAsString();
    return JsonPath.parse(responseJson).read("$.id");
  }

  String addLink(String personId, String companyId, String role, String title) throws Exception {

    String json =
        """
        {
          "role": "%s",
          "title": "%s"
        }
        """
            .formatted(role, title);

    MvcResult linkResult =
        mockMvc
            .perform(
                post("/api/v1/persons/" + personId + "/companies/" + companyId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNotEmpty())
            .andExpect(jsonPath("$.personId").value(personId))
            .andExpect(jsonPath("$.companyId").value(companyId))
            .andReturn();

    String responseJson = linkResult.getResponse().getContentAsString();
    return JsonPath.parse(responseJson).read("$.id");
  }
}
