package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Qualifier("filmGenreDbStorage")
    private final FilmGenreStorage filmGenreStorage;
    @Qualifier("mpaDbStorage")
    private final MpaStorage mpaStorage;

    @Override
    @Transactional
    public List<Film> getFilms() {
        final String sql = "SELECT id, \"name\", description, release_date, duration, mpa_id FROM films;";
        final List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
        films.forEach(film -> film.setGenres(filmGenreStorage.getFilmGenresByFilmId(film.getId())));
        return films;
    }

    @Override
    @Transactional
    public Film crateFilm(Film newFilm) {
        final String sql = "INSERT INTO films\n" +
                "(\"name\", description, release_date, duration, mpa_id)\n" +
                "VALUES(?, ?, ?, ?, ?)";
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        final PreparedStatementCreator preparedStatementCreator = connection -> {
            final PreparedStatement ps =
                    connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, newFilm.getName());
            ps.setString(2, newFilm.getDescription());
            ps.setDate(3, Date.valueOf(newFilm.getReleaseDate()));
            ps.setInt(4, newFilm.getDuration());
            final Mpa mpa = newFilm.getMpa();
            if (mpa == null || mpa.getId() == 0) {
                ps.setNull(5, Types.INTEGER);
            } else {
                mpaStorage.getMpaById(mpa.getId());
                ps.setInt(5, mpa.getId());
            }
            return ps;
        };
        jdbcTemplate.update(preparedStatementCreator, keyHolder);
        final long filmId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        newFilm.setId(filmId);
        final List<Genre> genres = newFilm.getGenres();
        filmGenreStorage.createFilmGenre(filmId, genres);
        return getFilmById(filmId);
    }

    @Override
    @Transactional
    public Film updateFilm(Film updatedFilm) {
        final String sql = "UPDATE films \n" +
                "SET \"name\"=?, description=?, release_date=?, duration=?, mpa_id=?\n" +
                "WHERE id=?;";
        final Long filmId = updatedFilm.getId();
        final PreparedStatementCreator preparedStatementCreator = connection -> {
            final PreparedStatement ps =
                    connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, updatedFilm.getName());
            ps.setString(2, updatedFilm.getDescription());
            ps.setDate(3, Date.valueOf(updatedFilm.getReleaseDate()));
            ps.setInt(4, updatedFilm.getDuration());
            final Mpa mpa = updatedFilm.getMpa();
            if (mpa == null || mpa.getId() == 0) {
                ps.setNull(5, Types.INTEGER);
            } else {
                mpaStorage.getMpaById(mpa.getId());
                ps.setInt(5, mpa.getId());
            }
            ps.setLong(6, filmId);
            return ps;
        };

        int numberOfRecordsAffected = jdbcTemplate.update(preparedStatementCreator);
        if (numberOfRecordsAffected == 0) {
            throw new NotFoundException(String.format("Фильма с id = %s нет", filmId));
        }
        filmGenreStorage.deleteFilmGenre(filmId, filmGenreStorage.getFilmGenresByFilmId(filmId));
        filmGenreStorage.createFilmGenre(filmId, updatedFilm.getGenres());
        return getFilmById(updatedFilm.getId());
    }

    @Override
    @Transactional
    public Film getFilmById(Long filmId) {
        final String sql = "SELECT * FROM films WHERE id = ?;";
        try {
            Film film = jdbcTemplate.queryForObject(sql, new Object[]{filmId},
                    new int[]{Types.BIGINT}, (rs, rowNum) -> makeFilm(rs));
            if (film != null) {
                film.setGenres(filmGenreStorage.getFilmGenresByFilmId(filmId));
            }
            return film;
        } catch (EmptyResultDataAccessException ex) {
            throw new NotFoundException(String.format("Фильма с id = %s нет", filmId));
        }
    }

    @Override
    @Transactional
    public List<Film> getFilmsByTheSpecifiedIds(List<Long> ids) {
        final SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("ids", ids);
        String sql = "SELECT * FROM films WHERE id IN (:ids);";
        final List<Film> films = namedParameterJdbcTemplate.query(sql, parameters, (rs, rowNum) -> makeFilm(rs));
        films.forEach(film -> film.setGenres(filmGenreStorage.getFilmGenresByFilmId(film.getId())));
        return films;
    }

    @Override
    @Transactional
    public List<Film> getRecommendationsForUser(Long userId) {
        String sqlGetMaximumIntersection = "SELECT l.user_id as user_id FROM likes as l " +
                "WHERE l.film_id in (SELECT film_id from likes l1 where l1.user_id = ?) " +
                "AND l.user_id <> ? " +
                "GROUP BY l.user_id " +
                "ORDER BY count(l.film_id);";
        String sqlGetRecommendations = "SELECT f.* FROM films as f " +
                "WHERE f.id in " +
                "(SELECT f1.id FROM films AS f1 LEFT JOIN likes AS l1 ON f1.id = l1.film_id " +
                "WHERE l1.user_id = ? " +
                "EXCEPT " +
                "SELECT f2.id FROM films AS f2 LEFT JOIN likes AS l2 ON f2.id = l2.film_id " +
                "WHERE l2.user_id = ?);";
        final List<Long> userIds = jdbcTemplate.query(sqlGetMaximumIntersection,
                (rs, rowNum) -> rs.getLong("user_id"), userId, userId);
        for (Long id : userIds) {
            List<Film> recommendedFilms = jdbcTemplate.query(sqlGetRecommendations,
                    (rs, rowNum) -> makeFilm(rs), id, userId);
            if (!recommendedFilms.isEmpty()) {
                recommendedFilms.forEach(film -> film.setGenres(filmGenreStorage.getFilmGenresByFilmId(film.getId())));
                return recommendedFilms;
            }
        }
        return new ArrayList<>();
    }


    private Film makeFilm(ResultSet rs) throws SQLException {
        return Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa((rs.getObject("mpa_id") == null)
                        ? null
                        : mpaStorage.getMpaById(rs.getInt("mpa_id"))
                ).build();
    }
}
