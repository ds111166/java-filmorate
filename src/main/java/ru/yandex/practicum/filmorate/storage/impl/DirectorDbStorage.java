package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component("directorDbStorage")
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public List<Director> getDirectors() {
        final String sql = "SELECT id, \"name\" FROM directors;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeDirector(rs));
    }

    @Override
    @Transactional
    public Director getDirectorById(Integer directorId) {
        final String sql = "SELECT id, \"name\" FROM directors where id = ?;";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{directorId},
                    new int[]{Types.INTEGER}, (rs, rowNum) -> makeDirector(rs));
        } catch (EmptyResultDataAccessException ex) {
            throw new NotFoundException(String.format("Режиссера с id = %s не существует", directorId));
        }
    }

    @Override
    @Transactional
    public Director crateDirector(Director newDirector) {
        final String sql = "INSERT INTO directors (\"name\") VALUES (?);";
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        final PreparedStatementCreator preparedStatementCreator = connection -> {
            final PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, newDirector.getName());
            return ps;
        };
        jdbcTemplate.update(preparedStatementCreator, keyHolder);
        final int directorId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        return getDirectorById(directorId);
    }

    @Override
    @Transactional
    public Director updateDirector(Director directorData) {
        final String sql = "UPDATE directors SET \"name\"=? WHERE id = ?";
        final Integer directorId = directorData.getId();
        final PreparedStatementCreator preparedStatementCreator = connection -> {
            final PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, directorData.getName());
            ps.setInt(2, directorId);
            return ps;
        };
        int numberOfRecordsAffected = jdbcTemplate.update(preparedStatementCreator);
        if (numberOfRecordsAffected == 0) {
            throw new NotFoundException(String.format("Режиссера с id = %s не существует", directorId));
        }
        return getDirectorById(directorId);
    }

    @Override
    @Transactional
    public void deleteDirector(Integer directorId) {
        final String sql = "DELETE FROM directors where id = ?;";
        jdbcTemplate.update(sql, directorId);
    }

    private Director makeDirector(ResultSet rs) throws SQLException {
        return Director.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name")).build();
    }
}
