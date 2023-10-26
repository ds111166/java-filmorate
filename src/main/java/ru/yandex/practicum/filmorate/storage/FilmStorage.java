package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.data.SearchType;
import ru.yandex.practicum.filmorate.data.SortType;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Set;

public interface FilmStorage {
    List<Film> getFilms();

    Film crateFilm(Film newFilm);

    Film updateFilm(Film updatedFilm);

    Film getFilmById(Long filmId);

    List<Film> getFilmsByTheSpecifiedIds(List<Long> ids);

    List<Film> getRecommendationsForUser(Long userId);

    List<Film> getFilmsByDirectorId(Integer directorId, SortType sortBy);

    List<Film> getCommonFilms(Long userId, Long friendId);

    void deleteFilm(Long filmId);

    List<Film> getTopPopularFilms(Integer count, Integer genreId, Integer year);


    List<Film> searchFilms(String query, Set<SearchType> searchTypes);
}
