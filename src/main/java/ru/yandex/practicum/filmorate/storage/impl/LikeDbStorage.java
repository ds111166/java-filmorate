package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component("likeDbStorage")
@RequiredArgsConstructor
public class LikeDbStorage implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO likes (film_id, user_id) VALUES(?, ?);";
        try {
            jdbcTemplate.update(sql, filmId, userId);
        } catch (DuplicateKeyException ignored) {
        }
    }

    @Override
    @Transactional
    public void deleteLike(Long filmId, Long userId) {
        String sql = "DELETE FROM likes WHERE film_id=? AND user_id=?;";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    @Transactional
    public Set<Like> getLikes() {
        String sql = "SELECT film_id, user_id FROM likes;";
        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> makeLike(rs)));
    }

    private Like makeLike(ResultSet rs) throws SQLException {
        return Like.builder()
                .filmId(rs.getLong("film_id"))
                .userId(rs.getLong("user_id")).build();
    }
}
