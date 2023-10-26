package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.data.SortType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validation.Marker;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getFilms() {
        log.info("Запрос на получение списка всех фильмов");
        final List<Film> films = filmService.getFilms();
        log.info("Отправлен список из {} фильмов", films.size());
        return films;
    }

    @GetMapping("/{id}")
    @Validated
    @ResponseStatus(HttpStatus.OK)
    public Film getFilmById(@PathVariable("id") @NotNull Long filmId) {
        log.info("Запрос на получение фильма с id: {}", filmId);
        final Film filmById = filmService.getFilmById(filmId);
        log.info("Отправлен - {}", filmById);
        return filmById;
    }

    @GetMapping("/popular")
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getTopPopularFilms(
            @RequestParam(value = "count", defaultValue = "10", required = false) Integer count,
            @RequestParam(value = "genreId", required = false) Integer genreId,
            @RequestParam(value = "year", required = false) Integer year) {
        log.info("Запрос на получение {} популярных фильмов жанра с id: {} за {} год", count, genreId, year);
        final List<Film> topPopularFilms = filmService.getTopPopularFilms(count, genreId, year);
        log.info("Размер отправленного списка популярных фильмов жанра с id: {} за {} год равен {}",
                genreId, year, topPopularFilms.size());
        return topPopularFilms;
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    @ResponseStatus(HttpStatus.CREATED)
    public Film crateFilm(@Valid @RequestBody Film newFilm) {
        log.info("Запрос на создание - {}", newFilm);
        final Film film = filmService.crateFilm(newFilm);
        log.info("Создан - {}", film);
        return film;
    }

    @PutMapping
    @Validated({Marker.OnUpdate.class})
    @ResponseStatus(HttpStatus.OK)
    public Film updateFilm(@Valid @RequestBody Film updatedFilm) {
        log.info("Запрос на обновление - {}", updatedFilm);
        final Film film = filmService.updateFilm(updatedFilm);
        log.info("Обновлён - {}", film);
        return film;
    }

    @DeleteMapping("/{filmId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFilm(@PathVariable("filmId") @NotNull Long filmId) {
        log.info("Запрос на удаление фильма с id: {}", filmId);
        filmService.deleteFilm(filmId);
        log.info("Фильм с id: {} удален", filmId);
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addLike(@PathVariable("id") @NotNull Long id, @PathVariable("userId") @NotNull Long userId) {
        log.info("Запрос на простановку лайка от пользователя с id: {} фильму с id: {}", userId, id);
        filmService.addLike(id, userId);
        log.info("Пользователь с id: {} поставил лайк фильму с  id: {}", userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteLike(@PathVariable("id") @NotNull Long id, @PathVariable("userId") @NotNull Long userId) {
        log.info("Запрос на удаления лайка от пользователя с id: {} фильму с id: {}", userId, id);
        filmService.deleteLike(id, userId);
        log.info("Пользователь с id: {} удалил лайк фильму с id: {}", userId, id);
    }

    @GetMapping("/director/{directorId}")
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getFilmsByDirectorId(@PathVariable("directorId") Integer directorId,
                                           @RequestParam(value = "sortBy") SortType sortBy) {
        log.info("Запрос на получение фильмов режиссера с id: {}, отсортированных по: {}", directorId, sortBy);
        final List<Film> directorsFilms = filmService.getFilmsByDirectorId(directorId, sortBy);
        log.info("Количество фильмов режиссера с id: {} равно: {}", directorId, directorsFilms.size());
        return directorsFilms;
    }

    @GetMapping("/common")
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getCommonFilms(@RequestParam(value = "userId") Long userId,
                                     @RequestParam(value = "friendId") Long friendId) {
        log.info("Запрос на получение общих фильмов пользователей с userId: {} и friendId: {}", userId, friendId);
        final List<Film> commonFilms = filmService.getCommonFilms(userId, friendId);
        log.info("Количество фильмов пользователей с userId: {} и friendId: {} равно: {}",
                userId, friendId, commonFilms.size());
        return commonFilms;
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<Film> searchFilms(@RequestParam(value = "query") String query,
                                     @RequestParam(value = "friendId") String by) {
        log.info("Запрос на поиск фильмов query: \"{}\", by: \"{}\"", query, by);
        final List<Film> films = filmService.searchFilms(query, by);
        log.info("По запросу: query: \"{}\", by: \"{}\" найдено фильмов: {}",
                query, by, films.size());
        return films;
    }
}
