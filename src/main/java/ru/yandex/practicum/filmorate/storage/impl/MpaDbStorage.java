package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

@Slf4j
@Component("mpaDbStorage")
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> getMpas() {
        final String sql = "SELECT id, \"name\" FROM mpa;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs));
    }

    @Override
    public Mpa getMpaById(Integer mpaId) {
        final String sql = "SELECT id, \"name\" FROM mpa where id = ?;";
        final Mpa mpa = jdbcTemplate.queryForObject(sql, new Object[]{mpaId},
                new int[]{Types.INTEGER}, (rs, rowNum) -> makeMpa(rs));
        if (mpa == null) {
            throw new NotFoundException(String.format("рейтинга MPA с id = %s нет", mpaId));
        }
        return mpa;
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        return Mpa.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name")).build();
    }
}
