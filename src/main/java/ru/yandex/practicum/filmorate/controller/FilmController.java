package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validation.Marker;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

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
    @Validated
    @ResponseStatus(HttpStatus.OK)
    public Film getFilmById(@PathVariable("id") @Min(1) @NotNull Long filmId) {
        return filmService.getFilmById(filmId);
    }

    @GetMapping("/popular?count={count}")
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getTopPopularFilms(@PathVariable(value = "count", required = false) @Min(0) Integer count) {
        return filmService.getTopPopularFilms(count);
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

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") @Min(1) @NotNull Long id, @PathVariable("friendId") @Min(1) @NotNull Long userId) {
        log.info("пользователь с id: {} поставил лайк фильму с  id: {}!", userId, id);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") @Min(1) @NotNull Long id, @PathVariable("friendId") @Min(1) @NotNull Long userId) {
        log.info("пользователь с id: {} удалил лайк фильму с id: {}!", userId, id);
        filmService.deleteLike(id, userId);
    }

}
