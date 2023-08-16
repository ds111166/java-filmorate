package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
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
    private long generatorId = 0;
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public ResponseEntity<Film> crateFilm(@Valid @RequestBody Film newFilm) {
        final long id = ++generatorId;
        newFilm.setId(id);
        films.put(newFilm.getId(), newFilm);
        log.info("добавлен - {}!", newFilm);
        return ResponseEntity.status(HttpStatus.CREATED).body(newFilm);
    }

    @PutMapping
    @Validated({Marker.OnUpdate.class})
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film updateFilm) {
        final long id = updateFilm.getId();
        if (!films.containsKey(id)) {
            throw new NotFoundException(String.format("фильма с id = %s нет", id));
        }
        films.put(id, updateFilm);
        log.info("обновлён - {}!", updateFilm);
        return ResponseEntity.ok(updateFilm);
    }

}
