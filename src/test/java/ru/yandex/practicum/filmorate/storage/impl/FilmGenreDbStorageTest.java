package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        final List<Integer> genreIds1 = film1.getGenreIds();
        assertEquals(genreIds1.size(), 0,
                "Не верное количество жанров фильма получено: " + genreIds1.size());
        final List<Integer> genresIds = Arrays.asList(1, 2, 4);
        filmGenreStorage.createFilmGenre(film.getId(), genresIds);
        final Film film2 = filmStorage.getFilmById(film.getId());
        final List<Integer> genreIds2 = film2.getGenreIds();
        assertEquals(genreIds2.size(), genresIds.size(),
                "Не верное количество жанров фильма получено: " + genreIds2.size());
        assertTrue(genreIds2.containsAll(genresIds), "Список id жанров назначенных фильму не верен");
    }

    @Test
    @Sql(scripts = {"classpath:test/clearing.sql"})
    void deleteFilmGenreTest() {
        createFilmGenreTest();
        final Film film1 = filmStorage.getFilmById(1L);
        final List<Integer> genresIds = Arrays.asList(1, 2, 4);
        final List<Integer> genreIds1 = film1.getGenreIds();
        assertEquals(genreIds1.size(), genresIds.size(),
                "Не верное количество жанров фильма получено: " + genreIds1.size());
        assertTrue(genreIds1.containsAll(genresIds), "Список id жанров назначенных фильму не верен");
        final List<Integer> genresIds2 = Arrays.asList(1, 4);
        filmGenreStorage.deleteFilmGenre(film1.getId(), genresIds2);
        final Film film2 = filmStorage.getFilmById(film1.getId());
        assertEquals(film2.getGenreIds().size(), 1,
                "После удаления двух жанров фильма " +
                        "не верное количество жанров фильма получено: " + film2.getGenreIds().size());
        assertTrue(film2.getGenreIds().contains(2), "После удаления двух жанров фильма " +
                "не верный id жанров фильма остался: " + film2.getGenreIds());
    }

    @Test
    @Sql(scripts = {"classpath:test/clearing.sql"})
    void getFilmGenreIdsByFilmIdTest() {
        deleteFilmGenreTest();
        final List<Integer> genreIds = filmGenreStorage.getFilmGenreIdsByFilmId(1L);
        assertEquals(genreIds.size(), 1,
                "Не верное количество жанров фильма получено: " + genreIds.size());
        assertTrue(genreIds.contains(2), "Не верный id жанров фильма получен: " + genreIds);
        final List<Integer> genreIds1 = filmGenreStorage.getFilmGenreIdsByFilmId(13332133L);
        assertEquals(genreIds1.size(), 0,
                "Не верное количество жанров фильма получено для фильма с не верным id: " + genreIds1.size());
    }

    @Test
    @Sql(scripts = {"classpath:test/clearing.sql"})
    void getFilmsIdsByGenreIdTest() {
        Film film1 = filmStorage.crateFilm(Film.builder().name("Name1").description("this film1").duration(120)
                .releaseDate(LocalDate.of(1990, 1, 1)).genreIds(Arrays.asList(1, 2, 4)).build());
        Film film2 = filmStorage.crateFilm(Film.builder().name("Name2").description("this film2").duration(100)
                .releaseDate(LocalDate.of(1990, 2, 2)).genreIds(Arrays.asList(3, 4)).build());
        Film film3 = filmStorage.crateFilm(Film.builder().name("Name3").description("this film3").duration(110)
                .releaseDate(LocalDate.of(1990, 3, 3)).genreIds(Arrays.asList(1, 4)).build());
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