package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface LikeStorage {
    void addLike(Long filmId, Long userId);
    void deleteFriend(Long filmId, Long userId);
    List<Long> getTopPopularFilms(int number);
}
