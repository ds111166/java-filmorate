package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FriendDbStorageTest {

    private final UserService userService;

    @Test
    @Sql(scripts = {"classpath:test/clearing.sql"})
    void addFriendTest() {
        User user1 = userService.createUser(User.builder().name("User1").email("user1@mail.ru").login("user1")
                .birthday(LocalDate.of(1991, 1, 1)).build());
        User user2 = userService.createUser(User.builder().name("User2").email("user2@mail.ru").login("user2")
                .birthday(LocalDate.of(1992, 2, 2)).build());
        User user3 = userService.createUser(User.builder().name("User3").email("user3@mail.ru").login("user3")
                .birthday(LocalDate.of(1993, 3, 3)).build());
        User user4 = userService.createUser(User.builder().name("User4").email("user4@mail.ru").login("user4")
                .birthday(LocalDate.of(1994, 4, 4)).build());
        userService.addFriend(user1.getId(), user2.getId());
        userService.addFriend(user1.getId(), user2.getId());
        userService.addFriend(user1.getId(), user2.getId());
        userService.addFriend(user1.getId(), user3.getId());
        userService.addFriend(user1.getId(), user4.getId());
        Set<Long> idsFriendsForUser1 = userService.getFriendsForUser(user1.getId())
                .stream().map(User::getId).collect(Collectors.toSet());
        assertEquals(idsFriendsForUser1.size(), 3,
                "Не верное количество пользователей друзей пользователя user1 получено: "
                        + idsFriendsForUser1.size());
        userService.addFriend(user2.getId(), user1.getId());
        userService.addFriend(user2.getId(), user1.getId());
        userService.addFriend(user2.getId(), user3.getId());
        final Set<Long> idsFriendsForUser2 = userService.getFriendsForUser(user2.getId())
                .stream().map(User::getId).collect(Collectors.toSet());
        assertEquals(idsFriendsForUser2.size(), 2,
                "Не верное количество пользователей друзей пользователя user2 получено: "
                        + idsFriendsForUser2.size());
        idsFriendsForUser1 = userService.getFriendsForUser(user1.getId())
                .stream().map(User::getId).collect(Collectors.toSet());
        assertEquals(idsFriendsForUser1.size(), 3,
                "Не верное количество пользователей друзей пользователя user1 получено: "
                        + idsFriendsForUser1.size());
        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.addFriend(-1L, 789L));
        assertTrue(Objects.requireNonNull(exception.getMessage()).contains("Пользователя с id = -1 не существует"),
                "При создания связи 'Дружба' с не верным id пользователя получено не верное исключение");

        final NotFoundException exception1 = assertThrows(
                NotFoundException.class,
                () -> userService.addFriend(user2.getId(), 789L));
        assertTrue(Objects.requireNonNull(exception1.getMessage()).contains("Пользователя с id = 789 не существует"),
                "При создания связи 'Дружба' с не верным id пользователя получено не верное исключение");
    }

    @Test
    @Sql(scripts = {"classpath:test/clearing.sql"})
    void deleteFriendTest() {
        addFriendTest();
        Set<Long> idsFriendsForUser1 = userService.getFriendsForUser(1L)
                .stream().map(User::getId).collect(Collectors.toSet());
        assertEquals(idsFriendsForUser1.size(), 3,
                "Не верное количество пользователей друзей пользователя user1 получено: "
                        + idsFriendsForUser1.size());
        userService.deleteFriend(2L, 1L);
        idsFriendsForUser1 = userService.getFriendsForUser(1L)
                .stream().map(User::getId).collect(Collectors.toSet());
        assertEquals(idsFriendsForUser1.size(), 3,
                "Не верное количество пользователей друзей пользователя user1 получено: "
                        + idsFriendsForUser1.size());
        userService.deleteFriend(1L, 3L);
        final Set<Long> idsFriendsForUser1_2 = userService.getFriendsForUser(1L)
                .stream().map(User::getId).collect(Collectors.toSet());
        assertEquals(idsFriendsForUser1_2.size(), 2,
                "Не верное количество пользователей друзей пользователя user1 получено: "
                        + idsFriendsForUser1_2.size());

        final NotFoundException exception1 = assertThrows(
                NotFoundException.class,
                () -> userService.deleteFriend(1L, 3333L));
        assertTrue(Objects.requireNonNull(exception1.getMessage()).contains("Пользователя с id = 3333 не существует"),
                "При удалении связи 'Дружба' с пользователем не верным id получено не верное исключение");
        final NotFoundException exception2 = assertThrows(
                NotFoundException.class,
                () -> userService.deleteFriend(1111111L, 3333L));
        assertTrue(Objects.requireNonNull(exception2.getMessage()).contains("Пользователя с id = 1111111 не существует"),
                "При удалении связи 'Дружба' с пользователем не верным id получено не верное исключение");
    }

    @Test
    @Sql(scripts = {"classpath:test/clearing.sql"})
    void getIdsFriendsForUserTest() {
        addFriendTest();
        Set<Long> idsFriendsForUser2 = userService.getFriendsForUser(2L)
                .stream().map(User::getId).collect(Collectors.toSet());
        assertEquals(idsFriendsForUser2.size(), 2,
                "Не верное количество пользователей друзей пользователя user2 получено: "
                        + idsFriendsForUser2.size());
        userService.addFriend(2L, 2L);
        idsFriendsForUser2 = userService.getFriendsForUser(2L)
                .stream().map(User::getId).collect(Collectors.toSet());
        assertEquals(idsFriendsForUser2.size(), 3,
                "Не верное количество пользователей друзей пользователя user2 получено: "
                        + idsFriendsForUser2.size());
        final NotFoundException exception1 = assertThrows(
                NotFoundException.class,
                () -> userService.getFriendsForUser(999999L)
                        .stream().map(User::getId).collect(Collectors.toSet()));
        assertTrue(Objects.requireNonNull(exception1.getMessage()).contains("Пользователя с id = 999999 не существует"),
                "При получении друзей для пользователя с неверным id получено не верное исключение");
    }

    @Test
    @Sql(scripts = {"classpath:test/clearing.sql"})
    void getMutualFriendsOfUsersTest() {
        addFriendTest();
        final User user1 = userService.getUserById(1L);
        final User user2 = userService.getUserById(2L);
        final User user3 = userService.getUserById(3L);
        final Set<Long> mutualFriendsOfUsers1And2 =
                userService.getMutualFriendsOfUsers(user1.getId(), user2.getId()).stream()
                        .map(User::getId).collect(Collectors.toSet());
        assertEquals(mutualFriendsOfUsers1And2.size(), 1,
                String.format("Не верное количество общих друзей у %s и %s получено: %s (%s)",
                        user1.getName(), user2.getName(), mutualFriendsOfUsers1And2.size(), mutualFriendsOfUsers1And2));
        final Set<Long> mutualFriendsOfUsers1And1 =
                userService.getMutualFriendsOfUsers(user1.getId(), user1.getId()).stream()
                        .map(User::getId).collect(Collectors.toSet());
        assertEquals(mutualFriendsOfUsers1And1.size(), 3,
                String.format("Не верное количество общих друзей у %s и %s получено: %s (%s)",
                        user1.getName(), user1.getName(), mutualFriendsOfUsers1And1.size(), mutualFriendsOfUsers1And1));
        final Set<Long> mutualFriendsOfUsers2And3 =
                userService.getMutualFriendsOfUsers(user2.getId(), user3.getId()).stream()
                        .map(User::getId).collect(Collectors.toSet());
        assertEquals(mutualFriendsOfUsers2And3.size(), 0,
                String.format("Не верное количество общих друзей у %s и %s получено: %s (%s)",
                        user2.getName(), user3.getName(), mutualFriendsOfUsers2And3.size(), mutualFriendsOfUsers2And3));
        final NotFoundException exception1 = assertThrows(
                NotFoundException.class,
                () -> userService.getMutualFriendsOfUsers(user2.getId(), 8788L).stream()
                        .map(User::getId).collect(Collectors.toSet()));
        assertTrue(Objects.requireNonNull(exception1.getMessage()).contains("Пользователя с id = 8788 не существует"),
                "При получении общих друзей для пользователей с не верным id получено не верное исключение");
    }
}