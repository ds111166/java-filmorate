package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface FilmGenreStorage {
    void createFilmGenre(long filmId, List<Genre> genres);

    void deleteFilmGenre(long filmId, List<Genre> genres);

    List<Genre> getFilmGenresByFilmId(long filmId);

    List<Long> getFilmsIdsByGenreId(int genreId);
}
