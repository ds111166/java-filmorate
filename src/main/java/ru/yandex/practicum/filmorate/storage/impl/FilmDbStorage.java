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
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.*;
import java.util.List;
import java.util.Objects;

@Component("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Qualifier("filmGenreDbStorage")
    private final FilmGenreStorage filmGenreStorage;

    @Override
    @Transactional
    public List<Film> getFilms() {
        final String sql = "SELECT id, \"name\", description, release_date, duration, mpa_id FROM films;";
        final List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
        films.forEach(film -> film.setGenreIds(filmGenreStorage.getFilmGenreIdsByFilmId(film.getId())));
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
            final Integer mpaId = newFilm.getMpaId();
            if ((mpaId == null)) {
                ps.setNull(5, Types.INTEGER);
            } else {
                ps.setInt(5, mpaId);
            }
            return ps;
        };
        jdbcTemplate.update(preparedStatementCreator, keyHolder);
        final long filmId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        newFilm.setId(filmId);
        final List<Integer> genreIds = newFilm.getGenreIds();
        filmGenreStorage.createFilmGenre(filmId, genreIds);
        return newFilm;
    }

    @Override
    @Transactional
    public Film updateFilm(Film updatedFilm) {
        final String sql = "UPDATE films \n" +
                "SET \"name\"=?, description=?, release_date=?, duration=?, mpa_id=?\n" +
                "WHERE id=?;";
        final Long filmId = updatedFilm.getId();
        int numberOfRecordsAffected = jdbcTemplate.update(sql, updatedFilm.getName(), updatedFilm.getDescription(),
                updatedFilm.getReleaseDate(), updatedFilm.getDuration(), updatedFilm.getMpaId(), filmId);
        if (numberOfRecordsAffected == 0) {
            throw new NotFoundException(String.format("Фильма с id = %s нет", filmId));
        }
        filmGenreStorage.deleteFilmGenre(filmId, filmGenreStorage.getFilmGenreIdsByFilmId(filmId));
        filmGenreStorage.createFilmGenre(filmId, updatedFilm.getGenreIds());
        return updatedFilm;
    }

    @Override
    @Transactional
    public Film getFilmById(Long filmId) {
        final String sql = "SELECT * FROM films WHERE id = ?;";
        try {
            Film film = jdbcTemplate.queryForObject(sql, new Object[]{filmId},
                    new int[]{Types.BIGINT}, (rs, rowNum) -> makeFilm(rs));
            if (film != null) {
                film.setGenreIds(filmGenreStorage.getFilmGenreIdsByFilmId(filmId));
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
        films.forEach(film -> film.setGenreIds(filmGenreStorage.getFilmGenreIdsByFilmId(film.getId())));
        return films;
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        return Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpaId(rs.getInt("mpa_id")).build();
    }
}
