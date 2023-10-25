package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {

    List<Director> getDirectors();

    Director getDirectorById(Integer directorId);

    Director crateDirector(Director newDirector);

    Director updateDirector(Director directorData);

    void deleteDirector(Integer directorId);
}
