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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmGenreDbStorageTest {

    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;
    @Qualifier("filmGenreDbStorage")
    private final FilmGenreStorage filmGenreStorage;

    @Test
    @Sql(scripts = {"classpath:test/clearing.sql"})
    void createFilmGenreTest() {

        Film film = filmStorage.crateFilm(Film.builder().name("Name1").description("this film1").duration(120)
                .releaseDate(LocalDate.of(1990, 1, 1)).build());
        final Film film1 = filmStorage.getFilmById(film.getId());
        final List<Genre> genres1 = film1.getGenres();
        assertEquals(genres1.size(), 0,
                "Не верное количество жанров фильма получено: " + genres1.size());
        final List<Genre> genres = Arrays.asList(new Genre(1, null),
                new Genre(2, null), new Genre(4, null));

        filmGenreStorage.createFilmGenre(film.getId(), genres);
        final Film film2 = filmStorage.getFilmById(film.getId());

        final List<Genre> genres2 = film2.getGenres();
        assertEquals(genres2.size(), genres.size(),
                "Не верное количество жанров фильма получено: " + genres2.size());
        assertTrue(genres2.stream().map(Genre::getId).collect(Collectors.toSet())
                        .containsAll(genres.stream().map(Genre::getId).collect(Collectors.toSet())),
                "Список id жанров назначенных фильму не верен");
        final NotFoundException exception1 = assertThrows(
                NotFoundException.class,
                () -> filmGenreStorage.createFilmGenre(989898L, genres));
        assertTrue(Objects.requireNonNull(exception1.getMessage()).contains("Фильма с id = 989898 нет"),
                "При отнесении фильм с не верным id к жанрам получено не верное исключение");
        final NotFoundException exception2 = assertThrows(
                NotFoundException.class,
                () -> filmGenreStorage.createFilmGenre(film.getId(),
                        Arrays.asList(new Genre(3, null), new Genre(98, null))));
        assertTrue(Objects.requireNonNull(exception2.getMessage()).contains("Жанра с id = 98 нет"),
                "При отнесении фильма к жанрам с не верным id получено не верное исключение");
    }

    @Test
    @Sql(scripts = {"classpath:test/clearing.sql"})
    void deleteFilmGenreTest() {
        createFilmGenreTest();
        final Film film1 = filmStorage.getFilmById(1L);
        final List<Genre> genres = Arrays.asList(new Genre(1, null),
                new Genre(2, null), new Genre(4, null));
        final List<Genre> genres1 = film1.getGenres();
        assertEquals(genres1.size(), genres.size(),
                "Не верное количество жанров фильма получено: " + genres1.size());
        final List<Integer> genreIds = genres.stream().map(Genre::getId).collect(Collectors.toList());
        final List<Integer> genreIds1 = genres1.stream().map(Genre::getId).collect(Collectors.toList());
        assertTrue(genreIds1.containsAll(genreIds), "Список id жанров назначенных фильму не верен");
        final List<Genre> genres2 = Arrays.asList(new Genre(1, null), new Genre(4, null));
        filmGenreStorage.deleteFilmGenre(film1.getId(), genres2);
        final Film film2 = filmStorage.getFilmById(film1.getId());
        assertEquals(film2.getGenres().size(), 1,
                "После удаления двух жанров фильма " +
                        "не верное количество жанров фильма получено: " + film2.getGenres().size());
        final Set<Integer> genres2new = film2.getGenres().stream().map(Genre::getId).collect(Collectors.toSet());
        assertTrue(genres2new.contains(2),
                "После удаления двух жанров фильма " +
                        "не верный id жанров фильма остался: " + genres2new);
        final NotFoundException exception2 = assertThrows(
                NotFoundException.class,
                () -> filmGenreStorage.deleteFilmGenre(film1.getId(),
                        Arrays.asList(new Genre(111122, null), new Genre(422, null))));
        assertTrue(Objects.requireNonNull(exception2.getMessage()).contains("Жанра с id = 111122 нет"),
                "При удалении жанров с неверными id от фильма получено не верное исключение");
    }

    @Test
    @Sql(scripts = {"classpath:test/clearing.sql"})
    void getFilmGenresByFilmIdTest() {
        deleteFilmGenreTest();
        final List<Genre> genres = filmGenreStorage.getFilmGenresByFilmId(1L);
        assertEquals(genres.size(), 1,
                "Не верное количество жанров фильма получено: " + genres.size());
        final Set<Integer> genreIds = genres.stream().map(Genre::getId).collect(Collectors.toSet());
        assertTrue(genreIds.contains(2), "Не верный id жанров фильма получен: " + genreIds);
        final List<Genre> genres3321 = filmGenreStorage.getFilmGenresByFilmId(3321L);
        assertEquals(genres3321.size(), 0,
                "Не верное количество жанров фильма получено для фильма с не верным id: " + genres3321.size());
    }

    @Test
    @Sql(scripts = {"classpath:test/clearing.sql"})
    void getFilmsIdsByGenreIdTest() {
        final List<Genre> genres1 = Arrays.asList(new Genre(1, null),
                new Genre(2, null), new Genre(4, null));
        final List<Genre> genres2 = Arrays.asList(new Genre(3, null), new Genre(4, null));
        final List<Genre> genres3 = Arrays.asList(new Genre(1, null), new Genre(4, null));
        Film film1 = filmStorage.crateFilm(Film.builder().name("Name1").description("this film1").duration(120)
                .releaseDate(LocalDate.of(1990, 1, 1)).genres(genres1).build());
        Film film2 = filmStorage.crateFilm(Film.builder().name("Name2").description("this film2").duration(100)
                .releaseDate(LocalDate.of(1990, 2, 2)).genres(genres2).build());
        Film film3 = filmStorage.crateFilm(Film.builder().name("Name3").description("this film3").duration(110)
                .releaseDate(LocalDate.of(1990, 3, 3)).genres(genres3).build());
        List<Long> filmIds = Arrays.asList(film1.getId(), film2.getId(), film3.getId());
        final List<Long> filmsIdsByGenreId = filmGenreStorage.getFilmsIdsByGenreId(4);
        assertEquals(filmsIdsByGenreId.size(), filmIds.size(),
                "Не верное количество фильмов  жанра 'Триллер' получено: " + filmsIdsByGenreId.size());
        assertTrue(filmsIdsByGenreId.containsAll(filmIds),
                "Не верное идентификаторы фильмов  жанра 'Триллер' получены: " + filmsIdsByGenreId);
        final List<Long> filmsIdsByGenreId1 = filmGenreStorage.getFilmsIdsByGenreId(6);
        assertEquals(filmsIdsByGenreId1.size(), 0,
                "Не верное количество фильмов  жанра 'Боевик' получено: " + filmsIdsByGenreId.size());
    }
}