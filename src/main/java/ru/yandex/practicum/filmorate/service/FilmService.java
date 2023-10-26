package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.data.EventType;
import ru.yandex.practicum.filmorate.data.Operation;
import ru.yandex.practicum.filmorate.data.SortType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public List<Film> getTopPopularFilms(Integer count) {
        Map<Long, Integer> filmIdToNumberLike = new HashMap<>();
        for (Film film : filmStorage.getFilms()) {
            filmIdToNumberLike.put(film.getId(), 0);
        }
        for (Like like : likeStorage.getLikes()) {
            final long filmId = like.getFilmId();
            if (filmIdToNumberLike.containsKey(filmId)) {
                filmIdToNumberLike.put(filmId, filmIdToNumberLike.get(filmId) + 1);
            } else {
                filmIdToNumberLike.put(filmId, 1);
            }
        }

        return filmStorage
                .getFilmsByTheSpecifiedIds(filmIdToNumberLike.entrySet().stream()
                        .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                        .map(Map.Entry::getKey)
                        .limit(count)
                        .collect(Collectors.toList()));
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
}
