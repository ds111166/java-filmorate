package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@WebMvcTest(UserController.class)
@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class UserControllerTest {

    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;

    @Test
    public void shouldFirstGetUsers() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        List<User> users = objectMapper.readValue(response, new TypeReference<>() {
        });
        assertEquals(users.size(), 0, "Неверное количество созданных пользователей!");
        List<User> newUsers = new ArrayList<>();
        newUsers.add(User.builder()
                .name("User1")
                .email("user1@mail.ru")
                .login("user1")
                .birthday(LocalDate.of(1990, 12, 31))
                .build());
        newUsers.add(User.builder()
                .name("User2")
                .email("user2@mail.ru")
                .login("user2")
                .birthday(LocalDate.of(1990, 12, 31))
                .build());
        newUsers.add(User.builder()
                .name("User3")
                .email("user3@mail.ru")
                .login("user3")
                .birthday(LocalDate.of(1990, 12, 31))
                .build());
        for (User user : newUsers) {
            final MvcResult mvcResult1 = mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(user)))
                    .andExpect(status().isCreated()).andReturn();
            final String contentAsString = mvcResult1.getResponse().getContentAsString();
            final User currentUser = objectMapper.readValue(contentAsString, User.class);
            user.setId(currentUser.getId());
        }
        mvcResult = mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        response = mvcResult.getResponse().getContentAsString();
        users = objectMapper.readValue(response, new TypeReference<>() {
        });
        final List<Long> usersIds = users.stream().map(User::getId).collect(Collectors.toList());
        final List<Long> newUserIds = newUsers.stream().map(User::getId).collect(Collectors.toList());
        assertEquals(usersIds.size(), newUserIds.size(), "количество созданных пользователей не корректно");
        assertTrue(usersIds.containsAll(newUserIds), "не верный список идентификатор пользователей");
    }

    @Test
    public void shouldCreateUser() throws Exception {
        User newUser = User.builder()
                .name("User")
                .email("name@mail.ru")
                .login("name_login")
                .birthday(LocalDate.of(1990, 12, 31))
                .build();

        final ResultActions resultActions = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("User"))
                .andExpect(jsonPath("$.email").value("name@mail.ru"))
                .andExpect(jsonPath("$.login").value("name_login"))
                .andExpect(jsonPath("$.birthday").value("1990-12-31"));
        final MvcResult mvcResult = resultActions.andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        final User user = objectMapper.readValue(response, User.class);
        resultActions.andExpect(jsonPath("$.id").value(user.getId()));
    }

    @Test
    public void shouldNotCreateUserWith_NotValidEmail() throws Exception {
        User newUser = User.builder()
                .name("User")
                .login("name_login")
                .birthday(LocalDate.of(1990, 12, 31))
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isBadRequest());
        newUser.setEmail("as_er_er.nero.cum@");
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotCreateUserWith_EmptyLogin() throws Exception {
        User newUser = User.builder()
                .name("User")
                .email("name@mail.ru")
                .birthday(LocalDate.of(1990, 12, 31))
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void shouldNotCreateUserWith_LoginContainingSpaces() throws Exception {
        User newUser = User.builder()
                .name("User")
                .email("name@mail.ru")
                .login(" name login 123 ")
                .birthday(LocalDate.of(1990, 12, 31))
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void shouldNotCreateUserWith_DateOfBirthFromFuture() throws Exception {
        User newUser = User.builder()
                .name("User")
                .email("name@mail.ru")
                .login("name_login")
                .birthday(LocalDate.now().plusDays(1))
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldCreateUserWith_EmptyName() throws Exception {
        User newUser = User.builder()
                .email("name@mail.ru")
                .login("name_login")
                .birthday(LocalDate.of(1990, 12, 31))
                .build();

        final ResultActions resultActions = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("name_login"))
                .andExpect(jsonPath("$.email").value("name@mail.ru"))
                .andExpect(jsonPath("$.login").value("name_login"))
                .andExpect(jsonPath("$.birthday").value("1990-12-31"));

        final MvcResult mvcResult = resultActions.andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        final User user = objectMapper.readValue(response, User.class);
        resultActions.andExpect(jsonPath("$.id").value(user.getId()));
    }

    @Test
    public void shouldCreateAndUpdateUser() throws Exception {
        User newUser = User.builder()
                .email("name@mail.ru")
                .login("name_login")
                .birthday(LocalDate.of(1990, 12, 31))
                .build();

        final ResultActions resultActions = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("name_login"))
                .andExpect(jsonPath("$.email").value("name@mail.ru"))
                .andExpect(jsonPath("$.login").value("name_login"))
                .andExpect(jsonPath("$.birthday").value("1990-12-31"));

        final MvcResult mvcResult = resultActions.andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        final User user = objectMapper.readValue(response, User.class);
        final Long userId = user.getId();
        resultActions.andExpect(jsonPath("$.id").value(userId));

        newUser.setId(userId);
        newUser.setName("name user");
        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("name user"))
                .andExpect(jsonPath("$.email").value("name@mail.ru"))
                .andExpect(jsonPath("$.login").value("name_login"))
                .andExpect(jsonPath("$.birthday").value("1990-12-31"));
    }

    @Test
    public void shouldCreateAndUpdateFilm_recordNotFound() throws Exception {
        User newUser = User.builder()
                .email("name@mail.ru")
                .login("name_login")
                .birthday(LocalDate.of(1990, 12, 31))
                .build();

        final ResultActions resultActions = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("name_login"))
                .andExpect(jsonPath("$.email").value("name@mail.ru"))
                .andExpect(jsonPath("$.login").value("name_login"))
                .andExpect(jsonPath("$.birthday").value("1990-12-31"));

        final MvcResult mvcResult = resultActions.andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        final User user = objectMapper.readValue(response, User.class);
        final Long userId = user.getId();
        resultActions.andExpect(jsonPath("$.id").value(userId));

        newUser.setId(9999L);
        newUser.setName("name user");
        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isNotFound());
    }
}