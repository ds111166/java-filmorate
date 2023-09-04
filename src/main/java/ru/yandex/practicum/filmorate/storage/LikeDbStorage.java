package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.Set;

@Component("likeDbStorage")
public class LikeDbStorage implements LikeStorage {
    @Override
    public void addLike(Long filmId, Long userId) {

    }

    @Override
    public void deleteLike(Long filmId, Long userId) {

    }

    @Override
    public Set<Like> getLikes() {
        return null;
    }
}
