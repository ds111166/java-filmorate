package ru.yandex.practicum.filmorate.storage.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.List;
import java.util.stream.Collectors;

@Component("filmGenreDbStorage")
@RequiredArgsConstructor
public class FilmGenreDbStorage implements FilmGenreStorage {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void createFilmGenre(long filmId, @NonNull List<Integer> genreIds) {
        if (genreIds.isEmpty()) {
            return;
        }
        final List<FilmGenre> filmGenres = genreIds.stream()
                .map(genreId -> new FilmGenre(filmId, genreId))
                .collect(Collectors.toList());
        jdbcTemplate.batchUpdate("INSERT INTO film_genre (film_id, genre_id) VALUES(?, ?)",
                filmGenres,
                100,
                (PreparedStatement ps, FilmGenre filmGenre) -> {
                    ps.setLong(1, filmGenre.getFilmId());
                    ps.setInt(2, filmGenre.getGenreId());
                });
    }

    @Override
    public void deleteFilmGenre(long filmId, @NonNull List<Integer> genreIds) {
        if (genreIds.isEmpty()) {
            return;
        }
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
}
