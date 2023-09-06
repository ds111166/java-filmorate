package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Genre> getGenres() {
        log.info("Запрос на получение списка всех жанров");
        final List<Genre> genres = genreService.getGenres();
        log.info("Отправлен список из {} жанров", genres.size());
        return genres;
    }

    @GetMapping("/{id}")
    @Validated
    @ResponseStatus(HttpStatus.OK)
    public Genre getGenreById(@PathVariable("id") @NotNull Integer genreId) {
        log.info("Запрос на получение жанра с id: {}", genreId);
        final Genre genreById = genreService.getGenreById(genreId);
        log.info("Отправлен - {}", genreById);
        return genreById;
    }
}
