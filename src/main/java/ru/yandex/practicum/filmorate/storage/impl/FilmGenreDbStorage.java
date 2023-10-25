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
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component("filmGenreDbStorage")
@RequiredArgsConstructor
public class FilmGenreDbStorage implements FilmGenreStorage {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Qualifier("genreDbStorage")
    private final GenreStorage genreStorage;

    @Override
    @Transactional
    public void createFilmGenre(long filmId, List<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return;
        }
        final Set<Integer> genreAllIds = genreStorage.getGenres().stream()
                .map(Genre::getId).collect(Collectors.toSet());
        for (Genre genre : genres) {
            final int genreId = genre.getId();
            if (!genreAllIds.contains(genreId)) {
                throw new NotFoundException(String.format("Жанра с id = %s нет", genreId));
            }
        }
        final List<FilmGenre> filmGenres = genres.stream()
                .map(genre -> new FilmGenre(filmId, genre.getId()))
                .collect(Collectors.toList());
        try {
            jdbcTemplate.batchUpdate("INSERT INTO film_genre (film_id, genre_id) VALUES(?, ?)",
                    filmGenres,
                    100,
                    (PreparedStatement ps, FilmGenre filmGenre) -> {
                        ps.setLong(1, filmGenre.getFilmId());
                        ps.setInt(2, filmGenre.getGenreId());
                    });
        } catch (DuplicateKeyException ignored) {
        } catch (DataIntegrityViolationException ex) {
            throw new NotFoundException(String.format("Фильма с id = %s нет", filmId));
        }
    }

    @Override
    @Transactional
    public void deleteFilmGenre(long filmId, List<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return;
        }
        final Set<Integer> genreAllIds = genreStorage.getGenres().stream()
                .map(Genre::getId).collect(Collectors.toSet());
        for (Genre genre : genres) {
            final int genreId = genre.getId();
            if (!genreAllIds.contains(genreId)) {
                throw new NotFoundException(String.format("Жанра с id = %s нет", genreId));
            }
        }
        final SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("genreIds", genres.stream().map(Genre::getId).collect(Collectors.toList()))
                .addValue("filmId", filmId, Types.BIGINT);
        final String sql = "DELETE FROM film_genre WHERE film_id = :filmId AND genre_id IN (:genreIds);";
        namedParameterJdbcTemplate.update(sql, parameters);
    }

    @Override
    @Transactional
    public List<Genre> getFilmGenresByFilmId(long filmId) {
        final String sql = "SELECT genre_id FROM film_genre WHERE film_id = ?;";
        final List<Integer> genreIds = jdbcTemplate.queryForList(sql,
                new Object[]{filmId}, new int[]{Types.BIGINT}, Integer.class);
        return genreStorage.getGenres().stream()
                .filter(genre -> genreIds.contains(genre.getId()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<Long> getFilmsIdsByGenreId(int genreId) {
        genreStorage.getGenreById(genreId);
        final String sql = "SELECT film_id FROM film_genre WHERE genre_id = ?;";
        return jdbcTemplate.queryForList(sql, new Object[]{genreId}, new int[]{Types.INTEGER}, Long.class);
    }
}
