package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

@Slf4j
@Component("genreDbStorage")
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public List<Genre> getGenres() {
        final String sql = "SELECT id, \"name\" FROM genres;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs));
    }

    @Override
    @Transactional
    public Genre getGenreById(Integer genreId) {
        final String sql = "SELECT id, \"name\" FROM genres where id = ?;";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{genreId},
                    new int[]{Types.INTEGER}, (rs, rowNum) -> makeGenre(rs));
        } catch (EmptyResultDataAccessException ex) {
            throw new NotFoundException(String.format("Жанра с id = %s нет", genreId));
        }
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name")).build();
    }
}
