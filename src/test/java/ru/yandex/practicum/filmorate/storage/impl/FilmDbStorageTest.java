package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {
    private final FilmDbStorage filmDbStorage;

    @Test
    @Sql(scripts = {"classpath:test/clean_film.sql"})
        //@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    void getFilms() {
        final List<Film> films = filmDbStorage.getFilms();
        assertEquals(films.size(), 0, "Не верное количество фильмов получено: " + films.size());

        Film film1 = filmDbStorage.crateFilm(Film.builder().name("Name1").description("this film1").duration(120)
                .releaseDate(LocalDate.of(1990, 1, 1)).build());
        Film film2 = filmDbStorage.crateFilm(Film.builder().name("Name2").description("this film2").duration(100)
                .releaseDate(LocalDate.of(1990, 2, 2)).build());
        Film film3 = filmDbStorage.crateFilm(Film.builder().name("Name3").description("this film3").duration(110)
                .releaseDate(LocalDate.of(1990, 3, 3)).build());
        Map<Long, Film> newFilms = Stream.of(film1, film2, film3)
                .collect(Collectors.toMap(Film::getId, film -> film, (a, b) -> b));
        Map<Long, Film> films1 = filmDbStorage.getFilms().stream()
                .collect(Collectors.toMap(Film::getId, film -> film, (a, b) -> b));
        assertEquals(films1.size(), newFilms.size(), "Не верное количество фильмов получено: " + films1.size());
        assertTrue(films1.keySet().containsAll(newFilms.keySet()), "Не верный список фильмов получен");
    }

    @Test
    //@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    @Sql(scripts = {"classpath:test/clean_film.sql"})
    void crateFilm() {
        Film film1 = filmDbStorage.crateFilm(Film.builder().name("Name1").description("this film1").duration(120)
                .releaseDate(LocalDate.of(1990, 1, 1)).build());
        assertNotNull(film1, "При создании фильма получен NULL");
        assertEquals(film1.getId(), 1, "Созданному фильму присвоен не верный id: " + film1.getId());
        Film film2 = filmDbStorage.crateFilm(Film.builder().name("Name2").description("this film2").duration(100)
                .releaseDate(LocalDate.of(1990, 2, 2)).mpaId(1).build());
        assertNotNull(film2, "При создании фильма получен NULL");
        assertEquals(film2.getId(), 2, "Созданному фильму присвоен не верный id: " + film2.getId());
        final DataIntegrityViolationException exception = assertThrows(
                DataIntegrityViolationException.class,
                () -> filmDbStorage.crateFilm(Film.builder().name("Name3").description("this film2").duration(110)
                        .releaseDate(LocalDate.of(1990, 3, 3)).mpaId(9999).build()));
        assertTrue(Objects.requireNonNull(exception.getMessage()).contains("Referential integrity constraint violation"),
                "При попытке создания фильма с не верным id MPA получено не верное исключение");
    }

    @Test
    //@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    @Sql(scripts = {"classpath:test/clean_film.sql"})
    void updateFilm() {
        Film film1 = filmDbStorage.crateFilm(Film.builder().name("Name1").description("this film1").duration(120)
                .releaseDate(LocalDate.of(1990, 1, 1)).build());
        assertNotNull(film1, "При создании фильма получен NULL");
        assertEquals(film1.getId(), 1, "Созданному фильму присвоен не верный id: " + film1.getId());
        film1.setName(film1.getName() + " ч.2");
        film1.setDescription(film1.getDescription() + " ч.2");
        film1.setDuration(film1.getDuration() + 15);
        film1.setReleaseDate(film1.getReleaseDate().plusDays(10));
        film1.setGenreIds(Arrays.asList(1, 2, 3));
        film1.setMpaId(4);
        filmDbStorage.updateFilm(film1);
        final Film film2 = filmDbStorage.getFilmById(film1.getId());
        assertEquals(film1.getId(), film2.getId(),
                "Метод обновления фильма вернул фильм с неверным id: " + film2.getId());
        assertEquals("Name1" + " ч.2", film2.getName(),
                "Метод обновления фильма вернул фильм с неверным названием: " + film2.getName());
        assertEquals("this film1" + " ч.2", film2.getDescription(),
                "Метод обновления фильма вернул фильм с неверным описанием: " + film2.getDescription());
        assertEquals(LocalDate.of(1990, 1, 1).plusDays(10), film2.getReleaseDate(),
                "Метод обновления фильма вернул фильм с неверной датой выхода: " + film2.getReleaseDate());
        assertEquals(120 + 15, film2.getDuration(),
                "Метод обновления фильма вернул фильм с неверной длительностью: " + film2.getDuration());
        assertEquals(film1.getMpaId(), film2.getMpaId(),
                "Метод обновления фильма вернул фильм с неверным idMpa : " + film2.getMpaId());
        assertTrue(film1.getGenreIds().containsAll(film2.getGenreIds()),
                "Метод обновления фильма вернул фильм с неверным списком ids жанров: " + film2.getGenreIds());
        film2.setId(9999L);
        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> filmDbStorage.updateFilm(film2));
        assertTrue(Objects.requireNonNull(exception.getMessage()).contains("Фильма с id = 9999 нет"),
                "При попытке обновления фильма с не верным id не верное исключение");


    }

    @Test
    @Sql(scripts = {"classpath:test/clean_film.sql"})
    void getFilmById() {
        Film film1 = filmDbStorage.crateFilm(Film.builder().name("Name1").description("this film1").duration(120)
                .releaseDate(LocalDate.of(1990, 1, 1))
                .genreIds(Arrays.asList(1, 2, 3)).mpaId(4).build());
        assertNotNull(film1, "При создании фильма получен NULL");
        assertEquals(film1.getId(), 1, "Созданному фильму присвоен не верный id: " + film1.getId());
        final Film film2 = filmDbStorage.getFilmById(film1.getId());
        assertEquals(film1.getId(), film2.getId(),
                "Метод получения фильма по id вернул фильм с неверным id: " + film2.getId());
        assertEquals(film1.getName(), film2.getName(),
                "Метод получения фильма по id вернул фильм с неверным названием: " + film2.getName());
        assertEquals(film1.getDescription(), film2.getDescription(),
                "Метод получения фильма по id вернул фильм с неверным описанием: " + film2.getDescription());
        assertEquals(film1.getReleaseDate(), film2.getReleaseDate(),
                "Метод получения фильма по id вернул фильм с неверной датой выхода: " + film2.getReleaseDate());
        assertEquals(film1.getDuration(), film2.getDuration(),
                "Метод получения фильма по id вернул фильм с неверной длительностью: " + film2.getDuration());
        assertEquals(film1.getMpaId(), film2.getMpaId(),
                "Метод получения фильма по id вернул фильм с неверным idMpa : " + film2.getMpaId());
        assertTrue(film1.getGenreIds().containsAll(film2.getGenreIds()),
                "Метод получения фильма по id вернул фильм с неверным списком ids жанров: " + film2.getGenreIds());

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> filmDbStorage.getFilmById(999222L));
        assertTrue(Objects.requireNonNull(exception.getMessage()).contains("Фильма с id = 999222 нет"),
                "При попытке получения фильма по id с не верным id не верное исключение");
    }

    @Test
    @Sql(scripts = {"classpath:test/clean_film.sql"})
    void getFilmsByTheSpecifiedIds() {
        Film film1 = filmDbStorage.crateFilm(Film.builder().name("Name1").description("this film1").duration(120)
                .releaseDate(LocalDate.of(1990, 1, 1)).build());
        Film film2 = filmDbStorage.crateFilm(Film.builder().name("Name2").description("this film2").duration(100)
                .releaseDate(LocalDate.of(1990, 2, 2)).build());
        Film film3 = filmDbStorage.crateFilm(Film.builder().name("Name3").description("this film3").duration(110)
                .releaseDate(LocalDate.of(1990, 3, 3)).build());
        Film film4 = filmDbStorage.crateFilm(Film.builder().name("Name4").description("this film4").duration(115)
                .releaseDate(LocalDate.of(1990, 4, 4)).build());

        Map<Long, Film> newFilms = Stream.of(film1, film2, film3)
                .collect(Collectors.toMap(Film::getId, film -> film, (a, b) -> b));
        List<Long> ids = new ArrayList<>(newFilms.keySet());
        Map<Long, Film> films1 = filmDbStorage.getFilmsByTheSpecifiedIds(ids).stream()
                .collect(Collectors.toMap(Film::getId, film -> film, (a, b) -> b));
        assertEquals(films1.size(), newFilms.size(), "Не верное количество фильмов получено: " + films1.size());
        assertTrue(films1.keySet().containsAll(newFilms.keySet()), "Не верный список фильмов получен");
    }
}