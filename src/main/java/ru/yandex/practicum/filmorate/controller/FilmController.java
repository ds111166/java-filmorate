package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.validation.Marker;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getFilms() {

        return filmService.getFilms();
    }
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Film getFilmById(@PathVariable("id") Long filmId) {
        return filmService.getFilmById(filmId);
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    @ResponseStatus(HttpStatus.CREATED)
    public Film crateFilm(@Valid @RequestBody Film newFilm) {
        final Film film = filmService.crateFilm(newFilm);
        log.info("добавлен - {}!", film);
        return film;
    }

    @PutMapping
    @Validated({Marker.OnUpdate.class})
    @ResponseStatus(HttpStatus.OK)
    public Film updateFilm(@Valid @RequestBody Film updatedFilm) {
        final Film film = filmService.updateFilm(updatedFilm);
        log.info("обновлён - {}!", film);
        return film;
    }

}
