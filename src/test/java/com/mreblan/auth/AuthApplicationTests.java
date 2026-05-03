package com.mreblan.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.mreblan.auth.controllers.AuthenticationController;
import com.mreblan.auth.controllers.TestController;
import com.mreblan.auth.services.impl.RedisService;   // <-- правильный импорт

import lombok.extern.slf4j.Slf4j;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.containsString;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    // H2 in-memory database
    "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.show-sql=true",
    // Отключаем Redis авто-конфигурацию
    "spring.data.redis.repositories.enabled=false",
    "spring.autoconfigure.exclude=" +
        "org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration," +
        "org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration," +
        "org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration",
    // Отключаем MinIO / S3
    "com.amazonaws.s3.enabled=false",
    "aws.s3.mock=true"
})
class AuthApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthenticationController authenticationController;

    @Autowired
    private TestController testController;

    // Мокаем RedisService, чтобы не было реальных вызовов Redis
    @MockBean
    private RedisService redisService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        // Настраиваем мок: isTokenRevoked всегда возвращает false (токен не отозван)
        when(redisService.isTokenRevoked(anyString())).thenReturn(false);
    }

    @Test
    void candidateTest() throws Exception {
        assertThat(authenticationController).isNotNull();
        assertThat(testController).isNotNull();

        final String username = "Lexa";
        final String password = "secr3t";

        String signUpJson = String.format("""
                {
                    "fio": "Петров Алексей Евгеньевич",
                    "username": "%s",
                    "email": "lexa@gmail.com",
                    "password": "%s",
                    "role": "CANDIDATE"
                }
                """, username, password);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signUpJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));

        String signInJson = String.format("""
                {
                    "username": "%s",
                    "password": "%s"
                }
                """, username, password);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signInJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        String token = objectMapper.readTree(jsonResponse).get("token").asText();
        log.info("Token: {}", token);

        mockMvc.perform(MockMvcRequestBuilders.get("/test/name")
                        .header("Authorization", "Bearer " + token))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("TEST NAME")));
    }

    @Test
    void hrTest() throws Exception {
        assertThat(authenticationController).isNotNull();
        assertThat(testController).isNotNull();

        final String username = "L3no4ka";
        final String password = "mySecr3tP4assword";

        String signUpJson = String.format("""
                {
                    "fio": "Иванова Елена Владимировна",
                    "username": "%s",
                    "email": "lenka@gmail.com",
                    "password": "%s",
                    "role": "HR"
                }
                """, username, password);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signUpJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));

        String signInJson = String.format("""
                {
                    "username": "%s",
                    "password": "%s"
                }
                """, username, password);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signInJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        String token = objectMapper.readTree(jsonResponse).get("token").asText();
        log.info("Token: {}", token);

        mockMvc.perform(MockMvcRequestBuilders.get("/test/name")
                        .header("Authorization", "Bearer " + token))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("TEST NAME")));
    }

    @Test
    void adminTest() throws Exception {
        assertThat(authenticationController).isNotNull();
        assertThat(testController).isNotNull();

        final String username = "XxX_van3k_xXx";
        final String password = "c00l_P455";

        String signUpJson = String.format("""
                {
                    "fio": "Ебланов Иван Иванович",
                    "username": "%s",
                    "email": "ivan@yandex.ru",
                    "password": "%s",
                    "role": "ADMIN"
                }
                """, username, password);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signUpJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));

        String signInJson = String.format("""
                {
                    "username": "%s",
                    "password": "%s"
                }
                """, username, password);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signInJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        String token = objectMapper.readTree(jsonResponse).get("token").asText();
        log.info("Token: {}", token);

        mockMvc.perform(MockMvcRequestBuilders.get("/test/name")
                        .header("Authorization", "Bearer " + token))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("TEST NAME")));
    }
}