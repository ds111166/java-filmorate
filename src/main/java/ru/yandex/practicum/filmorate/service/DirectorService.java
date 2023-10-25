package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorService {
    @Qualifier("directorDbStorage")
    private final DirectorStorage directorStorage;

    public List<Director> getDirectors() {
        return directorStorage.getDirectors();
    }

    public Director getDirectorById(Integer directorId) {
        return directorStorage.getDirectorById(directorId);
    }

    public Director crateDirector(Director newDirector) {
        return directorStorage.crateDirector(newDirector);
    }

    public Director updateDirector(Director directorData) {
        return directorStorage.updateDirector(directorData);
    }

    public void deleteDirector(Integer directorId) {
        directorStorage.deleteDirector(directorId);
    }
}
