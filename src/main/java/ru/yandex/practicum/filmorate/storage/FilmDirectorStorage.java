package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface FilmDirectorStorage {
    void createFilmDirector(long filmId, List<Director> directors);

    List<Director> getFilmDirectorByFilmId(Long filmId);

    void deleteFilmDirector(Long filmId, List<Director> directors);
}
