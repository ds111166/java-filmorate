package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<Film> getFilms() {
        //final String sql = "SELECT id, \"name\", description, release_date, duration, mpa_id FROM films;";
        /*final String sql = "SELECT fi.id, fi.\"name\", fi.description, fi.release_date," +
                " fi.duration, fi.mpa_id, fg.genre_id AS genre_id FROM films fi\n" +
                "LEFT JOIN film_genre fg ON fg.film_id = fi.id;";*/
        /*final String sql = "SELECT fi.id, fi.\"name\", fi.description, fi.release_date, fi.duration, fi.mpa_id, \n" +
                "(SELECT string_agg(cast(fg.genre_id AS text), ',')" +
                " FROM film_genre fg WHERE fg.film_id = fi.id) AS genre_ids\n" +
                "FROM films fi;";*/
        final String sql = "SELECT id, \"name\", description, release_date, duration, mpa_id FROM films;";
        final List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));

        return films;
    }

    @Override
    public Film crateFilm(Film newFilm) {
        return null;
    }

    @Override
    public Film updateFilm(Film updatedFilm) {
        return null;
    }

    @Override
    public Film getFilmById(Long filmId) {
        return null;
    }

    @Override
    public List<Film> getFilmsByTheSpecifiedIds(List<Long> ids) {
        return null;
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        final Film film = Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpaId(rs.getInt("mpa_id")).build();
        /*final String genreIds = rs.getString("genre_ids");
        if(genreIds != null) {
            film.getGenreIds().addAll(Arrays.stream(genreIds.split(","))
                    .map(Integer::parseInt)
                    .collect(Collectors.toSet()));
            Collections.sort(film.getGenreIds());
        }*/
        return film;
    }
}
