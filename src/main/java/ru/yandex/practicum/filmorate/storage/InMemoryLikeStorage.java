package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class InMemoryLikeStorage implements LikeStorage {
    private final Set<Like> likes;

    public InMemoryLikeStorage(Set<Like> likes) {
        this.likes = likes;
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

    @Override
    public List<Long> getTopPopularFilms(int number) {
        Map<Long, Integer> filmIdToNumberLike = new HashMap<>();
        for (Like like : likes) {
            final long filmId = like.getFilmId();
            if (filmIdToNumberLike.containsKey(filmId)) {
                filmIdToNumberLike.put(filmId, filmIdToNumberLike.get(filmId) + 1);
            } else {
                filmIdToNumberLike.put(filmId, 1);
            }
        }

        return filmIdToNumberLike.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .limit(number)
                .collect(Collectors.toList());
    }
}
