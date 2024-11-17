package com.mreblan.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.mreblan.auth.controllers.AuthenticationController;
import com.mreblan.auth.controllers.TestController;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.containsString;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class AuthApplicationTests {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthenticationController authenticationController;

    @Autowired
    private TestController testController;

	@Test
	void candidateTest() throws Exception {
		assertThat(authenticationController).isNotNull();
		assertThat(testController).isNotNull();

		final String username = "Lexa";
		final String password = "secr3t";

		StringBuilder jsonRequest = new StringBuilder();

		jsonRequest.append("{\n");
		jsonRequest.append("\"fio\": \"Петров Алексей Евгеньевич\",\n");
		jsonRequest.append(String.format("\"username\": \"%s\",\n", username));
		jsonRequest.append("\"email\": \"lexa@gmail.com\",\n");
		jsonRequest.append(String.format("\"password\": \"%s\",\n", password));
		jsonRequest.append("\"role\": \"CANDIDATE\"\n");
		jsonRequest.append("}");

		mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest.toString()))
                        .andDo(MockMvcResultHandlers.print())
                        .andExpect(status().isOk())
                        .andExpect(content().string(containsString("Пользователь создан")));

		jsonRequest = new StringBuilder();
		jsonRequest.append("{\n");
		jsonRequest.append(String.format("\"username\": \"%s\",\n", username));
		jsonRequest.append(String.format("\"password\": \"%s\"\n", password));
		jsonRequest.append("}");


		MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest.toString()))
                        .andDo(MockMvcResultHandlers.print())
                        .andExpect(status().isOk())
        				.andReturn();

        String token = result.getResponse().getContentAsString();
        log.info("RESPONSE CONTENT: {}", token);

		mockMvc.perform(MockMvcRequestBuilders
						.get("/test/name")
						.header("Authorization", String.format("Bearer %s", token)))
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

		StringBuilder jsonRequest = new StringBuilder();

		jsonRequest.append("{\n");
		jsonRequest.append("\"fio\": \"Иванова Елена Владимировна\",\n");
		jsonRequest.append(String.format("\"username\": \"%s\",\n", username));
		jsonRequest.append("\"email\": \"lenka@gmail.com\",\n");
		jsonRequest.append(String.format("\"password\": \"%s\",\n", password));
		jsonRequest.append("\"role\": \"HR\"\n");
		jsonRequest.append("}");

		mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest.toString()))
                        .andDo(MockMvcResultHandlers.print())
                        .andExpect(status().isOk())
                        .andExpect(content().string(containsString("Пользователь создан")));

		jsonRequest = new StringBuilder();
		jsonRequest.append("{\n");
		jsonRequest.append(String.format("\"username\": \"%s\",\n", username));
		jsonRequest.append(String.format("\"password\": \"%s\"\n", password));
		jsonRequest.append("}");


		MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest.toString()))
                        .andDo(MockMvcResultHandlers.print())
                        .andExpect(status().isOk())
        				.andReturn();

        String token = result.getResponse().getContentAsString();
        log.info("RESPONSE CONTENT: {}", token);

		mockMvc.perform(MockMvcRequestBuilders
						.get("/test/name")
						.header("Authorization", String.format("Bearer %s", token)))
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

		StringBuilder jsonRequest = new StringBuilder();

		jsonRequest.append("{\n");
		jsonRequest.append("\"fio\": \"Ебланов Иван Иванович\",\n");
		jsonRequest.append(String.format("\"username\": \"%s\",\n", username));
		jsonRequest.append("\"email\": \"ivan@yandex.ru\",\n");
		jsonRequest.append(String.format("\"password\": \"%s\",\n", password));
		jsonRequest.append("\"role\": \"ADMIN\"\n");
		jsonRequest.append("}");

		mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest.toString()))
                        .andDo(MockMvcResultHandlers.print())
                        .andExpect(status().isOk())
                        .andExpect(content().string(containsString("Пользователь создан")));

		jsonRequest = new StringBuilder();
		jsonRequest.append("{\n");
		jsonRequest.append(String.format("\"username\": \"%s\",\n", username));
		jsonRequest.append(String.format("\"password\": \"%s\"\n", password));
		jsonRequest.append("}");


		MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest.toString()))
                        .andDo(MockMvcResultHandlers.print())
                        .andExpect(status().isOk())
        				.andReturn();

        String token = result.getResponse().getContentAsString();
        log.info("RESPONSE CONTENT: {}", token);

		mockMvc.perform(MockMvcRequestBuilders
						.get("/test/name")
						.header("Authorization", String.format("Bearer %s", token)))
						.andDo(MockMvcResultHandlers.print())
						.andExpect(status().isOk())
						.andExpect(content().string(containsString("TEST NAME")));
		

	}
}
