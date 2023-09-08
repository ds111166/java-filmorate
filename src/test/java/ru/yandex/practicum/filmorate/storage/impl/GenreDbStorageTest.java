package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreDbStorageTest {
    @Qualifier("genreDbStorage")
    private final GenreStorage genreStorage;

    @Test
    void getGenresTest() {
        final Map<Integer, String> idToGenre = genreStorage.getGenres().stream()
                .collect(Collectors.toMap(Genre::getId, Genre::getName, (a, b) -> b));

        assertEquals(idToGenre.size(), 6, "Жанров количество не верное получено");
        assertEquals(idToGenre.get(1), "Комедия", "Не верный жанр с id=1 получен: " + idToGenre.get(1));
        assertEquals(idToGenre.get(2), "Драма", "Не верный жанр с id=2 получен: " + idToGenre.get(2));
        assertEquals(idToGenre.get(3), "Мультфильм", "Не верный жанр с id=3 получен: " + idToGenre.get(3));
        assertEquals(idToGenre.get(4), "Триллер", "Не верный жанр с id=4 получен: " + idToGenre.get(4));
        assertEquals(idToGenre.get(5), "Документальный", "Не верный жанр с id=5 получен: " + idToGenre.get(5));
        assertEquals(idToGenre.get(6), "Боевик", "Не верный жанр с id=6 получен: " + idToGenre.get(6));
    }

    @Test
    void getGenreByIdTest() {
        final Genre genre = genreStorage.getGenreById(1);
        assertEquals(genre.getId(), 1, "По id=1 жанр с неверным id = " + genre.getId() + " получен");
        assertEquals(genre.getName(), "Комедия",
                "по id=1 жанр с не верным наименованием: '" + genre.getName() + "' получен");
        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> genreStorage.getGenreById(9999));
        assertEquals("жанра с id = 9999 нет",
                exception.getMessage(),
                "При попытке получить жанра по не верному id не верное сообщение получено");
    }
}