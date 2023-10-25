package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.data.SortType;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> getFilms();

    Film crateFilm(Film newFilm);

    Film updateFilm(Film updatedFilm);

    Film getFilmById(Long filmId);

    List<Film> getFilmsByTheSpecifiedIds(List<Long> ids);

    List<Film> getRecommendationsForUser(Long userId);

    List<Film> getFilmsByDirectorId(Integer directorId, SortType sortBy);
}
