package com.catalogservice;

import com.catalogservice.dto.auth.RegisterRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("dev")
@Sql(statements = {"DELETE FROM user_roles", "DELETE FROM users"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class AuthControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void register_shouldReturn201_andLocation_andBody() throws Exception {
        String json = objectMapper.writeValueAsString(
                Map.of("email","testEmail@gmail.com",
                        "username","testUsername",
                        "password","testPassword1")
        );

        mockMvc.perform(post("/auth/register")
                        .contentType("application/json")
                        .accept("application/json")
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("testemail@gmail.com"))
                .andExpect(jsonPath("$.username").value("testUsername"));
    }

    @Test
    void register_shouldReturn400_shortPassword() throws Exception{
        String json = objectMapper.writeValueAsString(
                Map.of("email","testEmail@gmail.com",
                        "username","testUsername",
                        "password","t")
        );

        mockMvc.perform(post("/auth/register")
                        .contentType("application/json")
                        .accept("application/json")
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details.password").doesNotExist());
    }

    @Test
    void register_shouldReturn409_duplicateEmail() throws Exception {
        String json = objectMapper.writeValueAsString(
                Map.of("email","testemail@gmail.com",
                        "username","testUsername",
                        "password","testPassword1")
        );

        mockMvc.perform(post("/auth/register")
                        .contentType("application/json")
                        .accept("application/json")
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("testemail@gmail.com"))
                .andExpect(jsonPath("$.username").value("testUsername"));

        mockMvc.perform(post("/auth/register")
                        .contentType("application/json")
                        .accept("application/json")
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.code").value("UNIQUE_VIOLATION"))
                .andExpect(jsonPath("$.details.email").exists())
                .andExpect(jsonPath("$.details.email[0]").value("already exists"));
    }

    @Test
    void register_shouldReturn409_duplicateUsername() throws Exception {
        String json = objectMapper.writeValueAsString(
                Map.of("username","testUsername",
                        "email","testemail@gmail.com2",
                        "password","testPassword1")
        );

        String json2 = objectMapper.writeValueAsString(
                Map.of("username","testUsername",
                        "email","testemail@gmail.com1",
                        "password","testPassword1")
        );

        mockMvc.perform(post("/auth/register")
                        .contentType("application/json")
                        .accept("application/json")
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("testemail@gmail.com2"))
                .andExpect(jsonPath("$.username").value("testUsername"));

        mockMvc.perform(post("/auth/register")
                        .contentType("application/json")
                        .accept("application/json")
                        .content(json2))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.code").value("UNIQUE_VIOLATION"))
                .andExpect(jsonPath("$.details.username").exists())
                .andExpect(jsonPath("$.details.username[0]").value("already exists"));
    }
}
