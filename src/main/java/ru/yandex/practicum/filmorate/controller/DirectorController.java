package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.validation.Marker;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Director> getDirectors() {
        log.info("Запрос на получение списка всех режиссеров фильмов");
        final List<Director> directors = directorService.getDirectors();
        log.info("Количество режиссеров: {} ", directors.size());
        return directors;
    }

    @GetMapping("/{id}")
    @Validated
    @ResponseStatus(HttpStatus.OK)
    public Director getDirectorById(@PathVariable("id") @NotNull Integer directorId) {
        log.info("Запрос на получение режиссера с id: {}", directorId);
        final Director directorById = directorService.getDirectorById(directorId);
        log.info("Отправлен - {}", directorById);
        return directorById;
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    @ResponseStatus(HttpStatus.CREATED)
    public Director crateDirector(@Valid @RequestBody Director newDirector) {
        log.info("Запрос на создание - {}", newDirector);
        final Director director = directorService.crateDirector(newDirector);
        log.info("Создан - {}", director);
        return director;
    }

    @PutMapping
    @Validated({Marker.OnUpdate.class})
    @ResponseStatus(HttpStatus.OK)
    public Director updateDirector(@Valid @RequestBody Director directorData) {
        log.info("Запрос на обновление - {}", directorData);
        final Director updatedDirector = directorService.updateDirector(directorData);
        log.info("Обновлен - {}", updatedDirector);
        return updatedDirector;
    }

    @DeleteMapping("/{id}")
    @Validated
    @ResponseStatus(HttpStatus.OK)
    public void deleteDirector(@PathVariable("id") @NotNull Integer directorId) {
        log.info("Запрос на удаление режиссера с id: {}", directorId);
        directorService.deleteDirector(directorId);
        log.info("Удален режиссер с id: {}", directorId);
    }
}
