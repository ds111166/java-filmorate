package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.data.EventType;
import ru.yandex.practicum.filmorate.data.Operation;
import ru.yandex.practicum.filmorate.data.SearchType;
import ru.yandex.practicum.filmorate.data.SortType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {
    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;
    @Qualifier("likeDbStorage")
    private final LikeStorage likeStorage;
    private final EventService eventService;
    private final DirectorService directorService;

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilmById(Long filmId) {
        return filmStorage.getFilmById(filmId);
    }

    public Film crateFilm(Film newFilm) {
        return filmStorage.crateFilm(newFilm);
    }

    public Film updateFilm(Film updatedFilm) {
        return filmStorage.updateFilm(updatedFilm);
    }

    public void deleteFilm(Long filmId) {
        filmStorage.deleteFilm(filmId);
    }

    public void addLike(Long filmId, Long userId) {
        filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId);
        likeStorage.addLike(filmId, userId);
        eventService.createEvent(userId, EventType.LIKE, Operation.ADD, filmId);
    }

    public void deleteLike(Long filmId, Long userId) {
        filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId);
        likeStorage.deleteLike(filmId, userId);
        eventService.createEvent(userId, EventType.LIKE, Operation.REMOVE, filmId);
    }

    public List<Film> getTopPopularFilms(Integer count, Integer genreId, Integer year) {
        return filmStorage.getTopPopularFilms(count, genreId, year);
    }

    public List<Film> getFilmsByDirectorId(Integer directorId, SortType sortBy) {
        directorService.getDirectorById(directorId);
        return filmStorage.getFilmsByDirectorId(directorId, sortBy);
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        userStorage.getUserById(userId);
        userStorage.getUserById(friendId);
        return filmStorage.getCommonFilms(userId, friendId);
    }


    public List<Film> searchFilms(String query, SearchType[] by) {

        return filmStorage.searchFilms(query, new HashSet<>(Arrays.asList(by)));
    }
}
