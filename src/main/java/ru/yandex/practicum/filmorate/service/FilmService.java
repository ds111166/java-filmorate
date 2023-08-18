package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;

    public FilmService(FilmStorage filmStorage, LikeStorage likeStorage) {
        this.filmStorage = filmStorage;
        this.likeStorage = likeStorage;
    }

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
        likeStorage.addLike(filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        likeStorage.deleteLike(filmId, userId);
    }

    public List<Film> getTopPopularFilms(Integer count) {
        int number = (count == null) ? 10 : count;
        return filmStorage
                .getFilmsByTheSpecifiedIds(likeStorage.getTopPopularFilms(number));
    }
}
