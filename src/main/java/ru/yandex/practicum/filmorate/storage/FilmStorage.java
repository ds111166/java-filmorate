package ru.yandex.practicum.filmorate.storage;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> getFilms();
    Film crateFilm(Film newFilm);
    Film updateFilm(Film updatedFilm);
}
