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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping("/films")
public class FilmController {
    private int generatorId = 1;
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getFilms() {
        return films.values();
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public ResponseEntity<Film> crateFilm(@Valid @RequestBody Film newFilm) {
        newFilm.setId(generatorId);
        films.put(generatorId++, newFilm);
        return ResponseEntity.status(HttpStatus.CREATED).body(newFilm);
    }

    @PutMapping
    @Validated({Marker.OnUpdate.class})
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film updateFilm) throws NotFoundException {
        final int id = updateFilm.getId();
        if (!films.containsKey(id)) {
            throw new NotFoundException(String.format("фильма с id = %s нет", id));
        }
        films.put(id, updateFilm);
        return ResponseEntity.ok(updateFilm);
    }

}
