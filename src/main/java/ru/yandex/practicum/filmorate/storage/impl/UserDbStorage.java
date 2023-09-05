package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
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
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.*;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<User> getUsers() {
        final String sql = "SELECT * FROM users;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    @Transactional
    public User createUser(User newUser) {
        final String sql = "INSERT INTO users (login, \"name\", email, birthday)\n" +
                "VALUES(?, ?, ?, ?);";
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        final PreparedStatementCreator preparedStatementCreator = new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                final PreparedStatement ps =
                        connection.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, newUser.getLogin());
                ps.setString(2, newUser.getName());
                ps.setString(3, newUser.getEmail());
                ps.setDate(4, Date.valueOf(newUser.getBirthday()));
                return ps;
            }
        };

        jdbcTemplate.update(preparedStatementCreator, keyHolder);
        final long userId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        newUser.setId(userId);
        return newUser;
    }

    @Override
    @Transactional
    public User updateUser(User updatedUser) {
        final String sql = "UPDATE users SET login=?, \"name\"=?, email=?, birthday=? WHERE id=?;";
        int numberOfRecordsAffected = jdbcTemplate.update(sql, updatedUser.getLogin(), updatedUser.getName(),
                updatedUser.getEmail(), updatedUser.getBirthday(), updatedUser.getId());
        if (numberOfRecordsAffected == 0) {
            throw new NotFoundException(String.format("Пользователя с id = %s не существует", updatedUser.getId()));
        }
        return updatedUser;
    }

    @Override
    @Transactional
    public User getUserById(Long userId) {
        final String sql = "SELECT * FROM users WHERE id = ?;";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{userId},
                    new int[]{Types.BIGINT}, (rs, rowNum) -> makeUser(rs));
        } catch (EmptyResultDataAccessException ex) {
            throw new NotFoundException(String.format("Пользователя с id = %s не существует", userId));
        }
    }

    @Override
    @Transactional
    public List<User> getUsersByTheSpecifiedIds(Set<Long> ids) {
        final SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("ids", ids);
        String sql = "SELECT * FROM users WHERE id IN (:ids);";
        return namedParameterJdbcTemplate.query(sql, parameters, (rs, rowNum) -> makeUser(rs));
    }

    private User makeUser(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getLong("id"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .email(rs.getString("email"))
                .birthday(rs.getDate("birthday").toLocalDate()).build();
    }
}