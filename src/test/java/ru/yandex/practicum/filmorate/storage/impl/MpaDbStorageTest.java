package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaDbStorageTest {
    private final MpaService mpaService;

    @Test
    void getMpasTest() {
        final Map<Integer, String> idToMpa = mpaService.getMpas().stream()
                .collect(Collectors.toMap(Mpa::getId, Mpa::getName, (a, b) -> b));
        assertEquals(idToMpa.size(), 5, "Не верное количество рейтингов MPA получено");
        assertEquals(idToMpa.get(1), "G", "Не верный рейтинг с id=1 получен: " + idToMpa.get(1));
        assertEquals(idToMpa.get(2), "PG", "Не верный рейтинг с id=2 получен: " + idToMpa.get(2));
        assertEquals(idToMpa.get(3), "PG-13", "Не верный рейтинг с id=3 получен: " + idToMpa.get(3));
        assertEquals(idToMpa.get(4), "R", "Не верный рейтинг с id=4 получен: " + idToMpa.get(4));
        assertEquals(idToMpa.get(5), "NC-17", "Не верный рейтинг с id=5 получен: " + idToMpa.get(5));
    }

    @Test
    void getMpaByIdTest() {
        final Mpa mpa = mpaService.getMpaById(1);
        assertEquals(mpa.getId(), 1, "По id=1 получен рейтинг MPA с id = " + mpa.getId());
        assertEquals(mpa.getName(), "G",
                "по id=1 получен рейтинг MPA с не верным наименованием name: " + mpa.getName());
        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> mpaService.getMpaById(9999));
        assertEquals("рейтинга MPA с id = 9999 нет",
                exception.getMessage(),
                "При попытке получить рейтинг MPA по не верному id получено не верное сообщение");
    }
}