package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

@Component("filmGenreDbStorage")
@RequiredArgsConstructor
public class FilmGenreDbStorage implements FilmGenreStorage {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void createFilmGenre(long filmId, List<Integer> genreIds) {
        final String sql = "INSERT INTO film_genre (film_id, genre_id) VALUES(0, 0);";
        for (Integer genreId : genreIds) {
            jdbcTemplate.update(sql, filmId, genreId);
        }
    }

    @Override
    public void deleteFilmGenre(long filmId, List<Integer> genreIds) {
        final SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("genreIds", genreIds)
                .addValue("filmId", filmId, Types.BIGINT);
        String sql = "DELETE FROM film_genre WHERE film_id = :filmId AND genre_id IN (:genreIds);";
        namedParameterJdbcTemplate.update(sql, parameters);
    }

    @Override
    public List<Integer> getFilmGenreIdsByFilmId(long filmId) {
        final String sql = "SELECT genre_id FROM film_genre WHERE film_id = ?;";
        return jdbcTemplate.queryForList(sql, new Object[]{filmId}, new int[]{Types.BIGINT}, Integer.class);
    }

    @Override
    public List<Long> getFilmsIdsByGenreId(int genreId) {
        final String sql = "SELECT film_id FROM film_genre WHERE genre_id = ?;";
        return jdbcTemplate.queryForList(sql, new Object[]{genreId}, new int[]{Types.INTEGER}, Long.class);
    }

    private FilmGenre makeFilmGenre(ResultSet rs) throws SQLException {
        return FilmGenre.builder()
                .filmId(rs.getLong("film_id"))
                .genreId(rs.getInt("genre_id"))
                .build();
    }
}
