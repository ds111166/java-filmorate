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
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FriendDbStorageTest {

    @Qualifier("userDbStorage")
    private final UserStorage userStorage;
    @Qualifier("friendDbStorage")
    private final FriendStorage friendStorage;

    @Test
    @Sql(scripts = {"classpath:test/clearing.sql"})
    void addFriendTest() {
        User user1 = userStorage.createUser(User.builder().name("User1").email("user1@mail.ru").login("user1")
                .birthday(LocalDate.of(1991, 1, 1)).build());
        User user2 = userStorage.createUser(User.builder().name("User2").email("user2@mail.ru").login("user2")
                .birthday(LocalDate.of(1992, 2, 2)).build());
        User user3 = userStorage.createUser(User.builder().name("User3").email("user3@mail.ru").login("user3")
                .birthday(LocalDate.of(1993, 3, 3)).build());
        User user4 = userStorage.createUser(User.builder().name("User4").email("user4@mail.ru").login("user4")
                .birthday(LocalDate.of(1994, 4, 4)).build());
        friendStorage.addFriend(user1.getId(), user2.getId());
        friendStorage.addFriend(user1.getId(), user2.getId());
        friendStorage.addFriend(user1.getId(), user3.getId());
        friendStorage.addFriend(user1.getId(), user4.getId());
        Set<Long> idsFriendsForUser1 = friendStorage.getIdsFriendsForUser(user1.getId());
        assertEquals(idsFriendsForUser1.size(), 3,
                "Не верное количество пользователей друзей пользователя user1 получено: "
                        + idsFriendsForUser1.size());
        friendStorage.addFriend(user2.getId(), user1.getId());
        friendStorage.addFriend(user2.getId(), user1.getId());
        friendStorage.addFriend(user2.getId(), user3.getId());
        final Set<Long> idsFriendsForUser2 = friendStorage.getIdsFriendsForUser(user2.getId());
        assertEquals(idsFriendsForUser2.size(), 2,
                "Не верное количество пользователей друзей пользователя user2 получено: "
                        + idsFriendsForUser2.size());
        idsFriendsForUser1 = friendStorage.getIdsFriendsForUser(user1.getId());
        assertEquals(idsFriendsForUser1.size(), 3,
                "Не верное количество пользователей друзей пользователя user1 получено: "
                        + idsFriendsForUser1.size());
        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> friendStorage.addFriend(-1L, 789L));
        assertTrue(Objects.requireNonNull(exception.getMessage()).contains("Пользователя с id = -1 не существует"),
                "При создания связи 'Дружба' с не верным id пользователя получено не верное исключение");

        final NotFoundException exception1 = assertThrows(
                NotFoundException.class,
                () -> friendStorage.addFriend(user2.getId(), 789L));
        assertTrue(Objects.requireNonNull(exception1.getMessage()).contains("Пользователя с id = 789 не существует"),
                "При создания связи 'Дружба' с не верным id пользователя получено не верное исключение");
    }

    @Test
    @Sql(scripts = {"classpath:test/clearing.sql"})
    void deleteFriendTest() {
        addFriendTest();
        Set<Long> idsFriendsForUser1 = friendStorage.getIdsFriendsForUser(1L);
        assertEquals(idsFriendsForUser1.size(), 3,
                "Не верное количество пользователей друзей пользователя user1 получено: "
                        + idsFriendsForUser1.size());
        friendStorage.deleteFriend(2L, 1L);
        idsFriendsForUser1 = friendStorage.getIdsFriendsForUser(1L);
        assertEquals(idsFriendsForUser1.size(), 3,
                "Не верное количество пользователей друзей пользователя user1 получено: "
                        + idsFriendsForUser1.size());
        friendStorage.deleteFriend(1L, 3L);
        final Set<Long> idsFriendsForUser1_2 = friendStorage.getIdsFriendsForUser(1L);
        assertEquals(idsFriendsForUser1_2.size(), 2,
                "Не верное количество пользователей друзей пользователя user1 получено: "
                        + idsFriendsForUser1_2.size());

        final NotFoundException exception1 = assertThrows(
                NotFoundException.class,
                () -> friendStorage.deleteFriend(1L, 3333L));
        assertTrue(Objects.requireNonNull(exception1.getMessage()).contains("Пользователя с id = 3333 не существует"),
                "При удалении связи 'Дружба' с пользователем не верным id получено не верное исключение");
        final NotFoundException exception2 = assertThrows(
                NotFoundException.class,
                () -> friendStorage.deleteFriend(1111111L, 3333L));
        assertTrue(Objects.requireNonNull(exception2.getMessage()).contains("Пользователя с id = 1111111 не существует"),
                "При удалении связи 'Дружба' с пользователем не верным id получено не верное исключение");
    }

    @Test
    @Sql(scripts = {"classpath:test/clearing.sql"})
    void getIdsFriendsForUserTest() {
        addFriendTest();
        Set<Long> idsFriendsForUser2 = friendStorage.getIdsFriendsForUser(2L);
        assertEquals(idsFriendsForUser2.size(), 2,
                "Не верное количество пользователей друзей пользователя user2 получено: "
                        + idsFriendsForUser2.size());
        friendStorage.addFriend(2L, 2L);
        idsFriendsForUser2 = friendStorage.getIdsFriendsForUser(2L);
        assertEquals(idsFriendsForUser2.size(), 3,
                "Не верное количество пользователей друзей пользователя user2 получено: "
                        + idsFriendsForUser2.size());
        final NotFoundException exception1 = assertThrows(
                NotFoundException.class,
                () -> friendStorage.getIdsFriendsForUser(999999L));
        assertTrue(Objects.requireNonNull(exception1.getMessage()).contains("Пользователя с id = 999999 не существует"),
                "При получении друзей для пользователя с неыерным id получено не верное исключение");
    }

    @Test
    @Sql(scripts = {"classpath:test/clearing.sql"})
    void getMutualFriendsOfUsersTest() {
        addFriendTest();
        //friendStorage.addFriend(1L, 1L);
        final User user1 = userStorage.getUserById(1L);
        final User user2 = userStorage.getUserById(2L);
        final User user3 = userStorage.getUserById(3L);
        final Set<Long> mutualFriendsOfUsers1And2 =
                friendStorage.getMutualFriendsOfUsers(user1.getId(), user2.getId());
        assertEquals(mutualFriendsOfUsers1And2.size(), 1,
                String.format("Не верное количество обших друзей у %s и %s получено: %s (%s)",
                        user1.getName(), user2.getName(), mutualFriendsOfUsers1And2.size(), mutualFriendsOfUsers1And2));
        final Set<Long> mutualFriendsOfUsers1And1 =
                friendStorage.getMutualFriendsOfUsers(user1.getId(), user1.getId());
        assertEquals(mutualFriendsOfUsers1And1.size(), 3,
                String.format("Не верное количество обших друзей у %s и %s получено: %s (%s)",
                        user1.getName(), user1.getName(), mutualFriendsOfUsers1And1.size(), mutualFriendsOfUsers1And1));
        final Set<Long> mutualFriendsOfUsers2And3 =
                friendStorage.getMutualFriendsOfUsers(user2.getId(), user3.getId());
        assertEquals(mutualFriendsOfUsers2And3.size(), 0,
                String.format("Не верное количество обших друзей у %s и %s получено: %s (%s)",
                        user2.getName(), user3.getName(), mutualFriendsOfUsers2And3.size(), mutualFriendsOfUsers2And3));
        ;
        final NotFoundException exception1 = assertThrows(
                NotFoundException.class,
                () -> friendStorage.getMutualFriendsOfUsers(user2.getId(), 8788L));
        assertTrue(Objects.requireNonNull(exception1.getMessage()).contains("Пользователя с id = 8788 не существует"),
                "При получении общих друзей для пользователей с не верным id получено не верное исключение");
    }
}