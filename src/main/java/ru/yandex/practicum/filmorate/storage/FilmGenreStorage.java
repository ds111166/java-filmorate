package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface FilmGenreStorage {
    void createFilmGenre(long filmId, List<Integer> genreIds);

    void deleteFilmGenre(long filmId, List<Integer> genreIds);

    List<Integer> getFilmGenreIdsByFilmId(long filmId);

    List<Long> getFilmsIdsByGenreId(int genreId);
}
