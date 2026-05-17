package ru.mentee.power.crm.contactservice.adapter.in.rest;

import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
import ru.mentee.power.crm.contactservice.adapter.out.persistence.entity.InviteEntity;
import ru.mentee.power.crm.contactservice.adapter.out.persistence.repository.CompanyJpaRepository;
import ru.mentee.power.crm.contactservice.adapter.out.persistence.repository.InviteJpaRepository;
import ru.mentee.power.crm.contactservice.domain.model.InviteStatus;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
class InviteRestControllerIT {

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

  @Autowired private InviteJpaRepository inviteRepository;

  @Autowired private CompanyJpaRepository companyRepository;

  @BeforeEach
  void cleanUp() {
    companyRepository.deleteAll();
    inviteRepository.deleteAll();
  }

  @Test
  void createInviteShouldReturnCreatedWhenSuccess() throws Exception {
    CompanyEntity company = createCompanyEntity("TestCorp", "000000000000");
    companyRepository.save(company);
    String json =
        """
        {
        "email": "test@test.ru",
        "companyId": "%s",
        "role": "CUSTOMER",
        "expiresInHours": "24"
        }
        """
            .formatted(company.getId());

    mockMvc
        .perform(post("/api/v1/invites").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isCreated())
        .andExpect(header().string("location", matchesPattern("/api/v1/invites/.+")))
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("email").value("test@test.ru"))
        .andExpect(jsonPath("companyId").value(company.getId().toString()))
        .andExpect(jsonPath("companyName").value(company.getName()))
        .andExpect(jsonPath("role").value("CUSTOMER"))
        .andExpect(jsonPath("status").value("PENDING"))
        .andExpect(jsonPath("inviterPersonId").isEmpty())
        .andExpect(jsonPath("$.referralCode").exists())
        .andExpect(jsonPath("$.referralCode").value(matchesPattern("^[A-Za-z0-9]{8}$")))
        .andExpect(jsonPath("createdAt").exists())
        .andExpect(jsonPath("expiresAt").exists());
  }

  @ParameterizedTest
  @CsvSource({
    "testtest.ru, CUSTOMER, 24",
    ",               CUSTOMER, 24",
    "test@mail.com,  INVALID,  24",
    "test@mail.com,  CUSTOMER, 0",
    "test@mail.com,  CUSTOMER, 169"
  })
  void createInviteShouldReturnBadRequestWhenDataIsInvalid(String email, String role, int hours)
      throws Exception {
    UUID id = UUID.randomUUID();
    String json =
        """
        {
        "email": "%s",
        "companyId": "%s",
        "role": "%s",
        "expiresInHours": "%d"
        }
        """
            .formatted(email, id, role, hours);

    mockMvc
        .perform(post("/api/v1/invites").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
  }

  @Test
  void createInviteShouldReturnNotFoundWhenCompanyNotExists() throws Exception {
    UUID id = UUID.randomUUID();
    String json =
        """
        {
        "email": "test@test.ru",
        "companyId": "%s",
        "role": "CUSTOMER",
        "expiresInHours": 24
        }
        """
            .formatted(id);

    mockMvc
        .perform(post("/api/v1/invites").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errorCode").value("COMPANY_NOT_FOUND"));
  }

  @Test
  void createInviteShouldReturnConflictWhenInviteAlreadyExists() throws Exception {
    CompanyEntity company = createCompanyEntity("TestCorp", "000000000000");
    UUID companyId = company.getId();
    companyRepository.save(company);
    InviteEntity invite = createInviteEntity("test@test.ru", companyId, "CUSTOMER");
    inviteRepository.save(invite);

    String json =
        """
        {
        "email": "test@test.ru",
        "companyId": "%s",
        "role": "CUSTOMER",
        "expiresInHours": 24
        }
        """
            .formatted(companyId);

    mockMvc
        .perform(post("/api/v1/invites").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.errorCode").value("INVITE_CONFLICT"));
  }

  CompanyEntity createCompanyEntity(String name, String inn) {
    return new CompanyEntity(
        UUID.randomUUID(), name, inn, LocalDateTime.now(), LocalDateTime.now(), null);
  }

  InviteEntity createInviteEntity(String email, UUID companyId, String role) {
    InviteEntity invite = new InviteEntity();
    invite.setId(UUID.randomUUID());
    invite.setEmail(email);
    invite.setCompanyId(companyId);
    invite.setRole(role);
    invite.setStatus(InviteStatus.PENDING);
    invite.setReferralCode("11111111");
    invite.setInviterPersonId(UUID.randomUUID());
    invite.setCreatedAt(LocalDateTime.now());
    invite.setExpiresAt(invite.getCreatedAt().plusHours(24));
    return invite;
  }
}
