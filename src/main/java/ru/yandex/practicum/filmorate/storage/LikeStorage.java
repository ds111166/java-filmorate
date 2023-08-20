package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Like;

import java.util.Set;

public interface LikeStorage {
    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

    Set<Like> getLikes();
}
