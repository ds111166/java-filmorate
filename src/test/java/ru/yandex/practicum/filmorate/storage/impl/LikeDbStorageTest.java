package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class LikeDbStorageTest {
    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;
    @Qualifier("likeDbStorage")
    private final LikeStorage likeStorage;

    @Test
    @Sql(scripts = {"classpath:test/clearing.sql"})
    void addAndGetLikeTest() {
        final DataIntegrityViolationException exception = assertThrows(
                DataIntegrityViolationException.class,
                () -> likeStorage.addLike(13165L, 13232L));
        assertTrue(Objects.requireNonNull(exception.getMessage()).contains("Referential integrity constraint violation"),
                "При попытке создания лайка с не верным id фильма и пользователя получено не верное исключение");
        Film film1 = filmStorage.crateFilm(Film.builder().name("Name1").description("this film1").duration(120)
                .releaseDate(LocalDate.of(1990, 1, 1)).build());
        Film film2 = filmStorage.crateFilm(Film.builder().name("Name2").description("this film2").duration(100)
                .releaseDate(LocalDate.of(1990, 2, 2)).build());
        User user1 = userStorage.createUser(User.builder().name("User1").email("user1@mail.ru").login("user1")
                .birthday(LocalDate.of(1991, 1, 1)).build());
        User user2 = userStorage.createUser(User.builder().name("User2").email("user2@mail.ru").login("user2")
                .birthday(LocalDate.of(1992, 2, 2)).build());
        User user3 = userStorage.createUser(User.builder().name("User3").email("user3@mail.ru").login("user3")
                .birthday(LocalDate.of(1993, 3, 3)).build());
        likeStorage.addLike(film1.getId(), user1.getId());
        likeStorage.addLike(film1.getId(), user2.getId());
        likeStorage.addLike(film1.getId(), user3.getId());

        likeStorage.addLike(film2.getId(), user1.getId());
        likeStorage.addLike(film2.getId(), user2.getId());
        likeStorage.addLike(film2.getId(), user2.getId());

        final Set<Like> likes = likeStorage.getLikes();
        assertEquals(likes.size(), 5, "Неверное общее количество лайков получено: " + likes.size());
        final List<Like> likes1 = likes.stream()
                .filter(like -> like.getFilmId() == film1.getId()).collect(Collectors.toList());
        final List<Like> likes2 = likes.stream()
                .filter(like -> like.getFilmId() == film2.getId()).collect(Collectors.toList());
        assertEquals(likes1.size(), 3, "Неверное количество лайков для film1 получено" + likes1.size());
        assertEquals(likes2.size(), 2, "Неверное количество лайков для film2 получено" + likes2.size());


    }

    @Test
    @Sql(scripts = {"classpath:test/clearing.sql"})
    void deleteLikeTest() {
        likeStorage.deleteLike(2344L, 9999L);
        addAndGetLikeTest();
        likeStorage.deleteLike(2L, 2L);
        final Set<Like> likes = likeStorage.getLikes();
        assertEquals(likes.size(), 4, "Неверное общее количество лайков получено: " + likes.size());
        final List<Like> likes2 = likes.stream()
                .filter(like -> like.getFilmId() == 2L).collect(Collectors.toList());
        assertEquals(likes2.size(), 1, "Неверное количество лайков для film2 получено" + likes2.size());
    }

}