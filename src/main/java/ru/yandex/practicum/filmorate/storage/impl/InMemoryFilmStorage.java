package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.data.SortType;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("inMemoryFilmStorage")
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {
    protected long generatorId = 0;
    protected final Map<Long, Film> films;

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public List<Film> getFilmsByTheSpecifiedIds(List<Long> ids) {
        return films.values().stream()
                .filter(film -> ids.contains(film.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> getRecommendationsForUser(Long userId) {
        return null;
    }

    @Override
    public List<Film> getFilmsByDirectorId(Integer directorId, SortType sortBy) {
        return null;
    }

    @Override
    public List<Film> getCommonFilms(Long userId, Long friendId) {
        return null;
    }

    @Override
    public void deleteFilm(Long filmId) {

    }

    @Override
    public Film getFilmById(Long filmId) {
        if (!films.containsKey(filmId)) {
            throw new NotFoundException(String.format("Фильма с id = %s нет", filmId));
        }
        return films.get(filmId);
    }

    @Override
    public Film crateFilm(Film newFilm) {
        final long id = ++generatorId;
        newFilm.setId(id);
        films.put(newFilm.getId(), newFilm);
        return newFilm;
    }

    @Override
    public Film updateFilm(Film updatedFilm) {
        final long id = updatedFilm.getId();
        if (!films.containsKey(id)) {
            throw new NotFoundException(String.format("Фильма с id = %s нет", id));
        }
        films.put(id, updatedFilm);
        return updatedFilm;
    }
}
