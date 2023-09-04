package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    @Override
    public List<Film> getFilms() {
        return null;
    }

    @Override
    public Film crateFilm(Film newFilm) {
        return null;
    }

    @Override
    public Film updateFilm(Film updatedFilm) {
        return null;
    }

    @Override
    public Film getFilmById(Long filmId) {
        return null;
    }

    @Override
    public List<Film> getFilmsByTheSpecifiedIds(List<Long> ids) {
        return null;
    }
}
