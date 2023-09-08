package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {

    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    @Test
    @Sql(scripts = {"classpath:test/clearing.sql"})
    void get1UsersTest() {
        final List<User> users = userStorage.getUsers();
        assertEquals(users.size(), 0, "Не верное количество пользователей получено: " + users.size());
        User user1 = userStorage.createUser(User.builder().name("User1").email("user1@mail.ru").login("user1")
                .birthday(LocalDate.of(1991, 1, 1)).build());
        User user2 = userStorage.createUser(User.builder().name("User2").email("user2@mail.ru").login("user2")
                .birthday(LocalDate.of(1992, 2, 2)).build());
        User user3 = userStorage.createUser(User.builder().name("User3").email("user3@mail.ru").login("user3")
                .birthday(LocalDate.of(1993, 3, 3)).build());
        Map<Long, User> newUsers = Stream.of(user1, user2, user3)
                .collect(Collectors.toMap(User::getId, user -> user, (a, b) -> b));
        Map<Long, User> users1 = userStorage.getUsers().stream()
                .collect(Collectors.toMap(User::getId, user -> user, (a, b) -> b));
        assertEquals(users1.size(), newUsers.size(), "Не верное количество пользователей получено: " + users1.size());
        assertTrue(users1.keySet().containsAll(newUsers.keySet()), "Не верный список пользователей получен");
    }

    @Test
    @Sql(scripts = {"classpath:test/clearing.sql"})
    void createUserTest() {
        User user = userStorage.createUser(User.builder().name("User1").email("user1@mail.ru").login("user1")
                .birthday(LocalDate.of(1991, 1, 1)).build());
        assertNotNull(user, "При создании пользователя получен NULL");
        assertEquals(user.getId(), 1, "Созданному пользователю присвоен не верный id: " + user.getId());
    }

    @Test
    @Sql(scripts = {"classpath:test/clearing.sql"})
    void updateUserTest() {
        User user = userStorage.createUser(User.builder().name("User1").email("user1@mail.ru").login("user1")
                .birthday(LocalDate.of(1991, 1, 1)).build());
        assertEquals(user.getEmail(), "user1@mail.ru", "Созданному пользователю присвоен не не верный email: " + user.getEmail());
        user.setEmail("user@mail.ru");
        final User user1 = userStorage.updateUser(user);
        assertEquals(user.getId(), user1.getId(), "Метод обновления пользователя вернул не верный id: " + user1.getId());
        assertEquals(user1.getEmail(), "user@mail.ru", "Обновленному пользователю присвоен не верный email: " + user1.getEmail());
        user.setId(989898L);
        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userStorage.updateUser(user));
        assertTrue(Objects.requireNonNull(exception.getMessage()).contains("Пользователя с id = 989898 не существует"),
                "При попытке обновления пользователя с не верным id получено не верное исключение");
    }

    @Test
    @Sql(scripts = {"classpath:test/clearing.sql"})
    void getUserByIdTest() {
        createUserTest();
        final User user = userStorage.getUserById(1L);
        assertNotNull(user, "Метод получения пользователя по id вернул NULL");
        assertEquals(user.getEmail(), "user1@mail.ru",
                "Метод получения пользователя по id вернул пользователя с не верным email: " + user.getEmail());
        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userStorage.getUserById(7656432L));
        assertTrue(Objects.requireNonNull(exception.getMessage()).contains("Пользователя с id = 7656432 не существует"),
                "При попытке получения пользователя с не верным id получено не верное исключение");
    }

    @Test
    @Sql(scripts = {"classpath:test/clearing.sql"})
    void getUsersByTheSpecifiedIdsTest() {
        User user1 = userStorage.createUser(User.builder().name("User1").email("user1@mail.ru").login("user1")
                .birthday(LocalDate.of(1991, 1, 1)).build());
        userStorage.createUser(User.builder().name("User2").email("user2@mail.ru").login("user2")
                .birthday(LocalDate.of(1992, 2, 2)).build());
        User user3 = userStorage.createUser(User.builder().name("User3").email("user3@mail.ru").login("user3")
                .birthday(LocalDate.of(1993, 3, 3)).build());
        User user4 = userStorage.createUser(User.builder().name("User4").email("user4@mail.ru").login("user4")
                .birthday(LocalDate.of(1994, 4, 4)).build());

        Map<Long, User> newUsers = Stream.of(user1, user3, user4)
                .collect(Collectors.toMap(User::getId, user -> user, (a, b) -> b));

        Map<Long, User> users1 = userStorage.getUsersByTheSpecifiedIds(newUsers.keySet()).stream()
                .collect(Collectors.toMap(User::getId, user -> user, (a, b) -> b));
        assertEquals(users1.size(), newUsers.size(), "Не верное количество пользователей получено: " + users1.size());
        assertTrue(users1.keySet().containsAll(newUsers.keySet()), "Не верный список пользователей получен");
    }
}