package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.Set;

@Component
public class InMemoryLikeStorage implements LikeStorage {

    private final Set<Like> likes;

    public InMemoryLikeStorage(Set<Like> likes) {
        this.likes = likes;
    }

    @Override
    public Set<Like> getLikes() {
        return likes;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        Like like = Like.builder().filmId(filmId).userId(userId).build();
        likes.add(like);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        Like like = Like.builder().filmId(filmId).userId(userId).build();
        likes.remove(like);
    }
}
