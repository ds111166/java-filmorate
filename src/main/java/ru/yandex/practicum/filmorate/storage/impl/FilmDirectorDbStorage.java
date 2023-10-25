package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.FilmDirector;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.FilmDirectorStorage;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.List;
import java.util.stream.Collectors;

@Component("filmDirectorDbStorage")
@RequiredArgsConstructor
public class FilmDirectorDbStorage implements FilmDirectorStorage {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Qualifier("directorDbStorage")
    private final DirectorStorage directorStorage;

    @Override
    @Transactional
    public void createFilmDirector(long filmId, List<Director> directors) {
        if (directors == null || directors.isEmpty()) {
            return;
        }
        final List<Integer> allDirectorsIds = directorStorage.getDirectors().stream()
                .map(Director::getId)
                .collect(Collectors.toList());
        for (Director director : directors) {
            final Integer directorId = director.getId();
            if (!allDirectorsIds.contains(directorId)) {
                throw new NotFoundException(String.format("Режиссера с id = %s не существует", directorId));
            }
        }
        final List<FilmDirector> filmDirectors = directors.stream()
                .map(director -> new FilmDirector(filmId, director.getId()))
                .collect(Collectors.toList());
        try {
            jdbcTemplate.batchUpdate("INSERT INTO film_director (film_id, director_id) VALUES(?, ?)",
                    filmDirectors,
                    100,
                    (PreparedStatement ps, FilmDirector filmDirector) -> {
                        ps.setLong(1, filmDirector.getFilmId());
                        ps.setInt(2, filmDirector.getDirectorId());
                    });
        } catch (DuplicateKeyException ignored) {
        } catch (DataIntegrityViolationException ex) {
            throw new NotFoundException(String.format("Фильма с id = %s нет", filmId));
        }
    }

    @Override
    @Transactional
    public List<Director> getFilmDirectorByFilmId(Long filmId) {
        final String sql = "SELECT director_id FROM film_director WHERE film_id = ?;";
        final List<Integer> directorIds = jdbcTemplate.queryForList(sql,
                new Object[]{filmId}, new int[]{Types.BIGINT}, Integer.class);
        return directorStorage.getDirectors().stream()
                .filter(director -> directorIds.contains(director.getId()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteFilmDirector(Long filmId, List<Director> directors) {
        if (directors == null || directors.isEmpty()) {
            return;
        }
        final SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("directorIds", directors.stream()
                        .map(Director::getId)
                        .collect(Collectors.toList()))
                .addValue("filmId", filmId, Types.BIGINT);
        final String sql = "DELETE FROM film_director WHERE film_id = :filmId AND director_id IN (:directorIds);";
        namedParameterJdbcTemplate.update(sql, parameters);
    }
}
