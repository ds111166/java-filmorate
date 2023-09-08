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
        final Set<Long> idsFriendsForUser1 = friendStorage.getIdsFriendsForUser(user1.getId());
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
    }

    @Test
    @Sql(scripts = {"classpath:test/clearing.sql"})
    void getIdsFriendsForUserTest() {
    }

    @Test
    @Sql(scripts = {"classpath:test/clearing.sql"})
    void getMutualFriendsOfUsersTest() {
        addFriendTest();
        friendStorage.addFriend(1L, 1L);
        final User user1 = userStorage.getUserById(1L);
        final User user2 = userStorage.getUserById(2L);
        final Set<Long> mutualFriendsOfUsers1And2 =
                friendStorage.getMutualFriendsOfUsers(user1.getId(), user2.getId());
        assertEquals(mutualFriendsOfUsers1And2.size(), 222,
                String.format("Не верное количество обших друзей у %s и %s получено: %s (%s)",
                        user1.getName(), user2.getName(), mutualFriendsOfUsers1And2.size(), mutualFriendsOfUsers1And2));
    }
}