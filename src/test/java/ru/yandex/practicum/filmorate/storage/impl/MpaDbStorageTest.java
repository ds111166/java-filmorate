package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MpaDbStorageTest {
    private final MpaDbStorage mpaDbStorage;

    @Test
    void getMpas() {
        final Map<Integer, String> mpas = mpaDbStorage.getMpas().stream()
                .collect(Collectors.toMap(Mpa::getId, Mpa::getName, (a, b) -> b));
        assertEquals(mpas.size(), 5, "Не верное количество рейтингов MPA получено");
        assertEquals(mpas.get(1), "G", "Не верный рейтинг с id=1 получен: " + mpas.get(1));
        assertEquals(mpas.get(2), "PG", "Не верный рейтинг с id=2 получен: " + mpas.get(2));
        assertEquals(mpas.get(3), "PG-13", "Не верный рейтинг с id=3 получен: " + mpas.get(3));
        assertEquals(mpas.get(4), "R", "Не верный рейтинг с id=4 получен: " + mpas.get(4));
        assertEquals(mpas.get(5), "NC-17", "Не верный рейтинг с id=5 получен: " + mpas.get(5));
    }

    @Test
    void getMpaById() {
        final Mpa mpa = mpaDbStorage.getMpaById(1);
        assertEquals(mpa.getId(), 1, "По id=1 получен рейтинг MPA с id = " + mpa.getId());
        assertEquals(mpa.getName(), "G",
                "по id=1 получен рейтинг MPA с не верным наименованием name: " + mpa.getName());
        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> mpaDbStorage.getMpaById(9999));
        assertEquals("рейтинга MPA с id = 9999 нет",
                exception.getMessage(),
                "При попытке получить рейтинг MPA по не верному id получено не верное сообщение");

    }
}