package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    private final MpaService mpaService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Mpa> getMpas() {
        log.info("Запрос на получение списка всех рейтингов MPA");
        final List<Mpa> mpas = mpaService.getMpas();
        log.info("Отправлен список из {} рейтингов MPA", mpas.size());
        return mpas;
    }

    @GetMapping("/{id}")
    @Validated
    @ResponseStatus(HttpStatus.OK)
    public Mpa getMpaById(@PathVariable("id") @NotNull Integer mpaId) {
        log.info("Запрос на получение рейтинга MPA с id: {}", mpaId);
        final Mpa mpaById = mpaService.getMpaById(mpaId);
        log.info("Отправлен - {}", mpaById);
        return mpaById;
    }
}
