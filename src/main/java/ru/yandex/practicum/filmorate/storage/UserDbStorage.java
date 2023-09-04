package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Types;
import java.util.Arrays;
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
        return jdbcTemplate.query("SELECT * FROM users;",
                (rs, rowNum) -> makeUser(rs));
    }

    @Override
    @Transactional
    public User createUser(User newUser) {
        final PreparedStatementCreatorFactory pscf =
                new PreparedStatementCreatorFactory(
                        "INSERT INTO users (login, \"name\", email, birthday)\n" +
                                "VALUES(?, ?, ?, ?);",
                        Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.DATE);
        pscf.setReturnGeneratedKeys(true);
        final PreparedStatementCreator psc =
                pscf.newPreparedStatementCreator(
                        Arrays.asList(
                                newUser.getLogin(),
                                newUser.getName(),
                                newUser.getEmail(),
                                newUser.getBirthday()));
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(psc, keyHolder);
        final long userId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        newUser.setId(userId);
        return newUser;
    }

    @Override
    @Transactional
    public User updateUser(User updatedUser) {
        String sql = "UPDATE users SET login=?, \"name\"=?, email=?, birthday=? WHERE id=?;";
        jdbcTemplate.update(sql, updatedUser.getLogin(), updatedUser.getName(),
                updatedUser.getEmail(), updatedUser.getBirthday(), updatedUser.getId());
        return updatedUser;
    }

    @Override
    @Transactional
    public User getUserById(Long userId) {
        final SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE id = ?;", userId);
        if(sqlRowSet.next()){
            return User.builder()
                    .id(sqlRowSet.getLong("id"))
                    .login(sqlRowSet.getString("login"))
                    .name(sqlRowSet.getString("name"))
                    .email(sqlRowSet.getString("email"))
                    .birthday(Objects.requireNonNull(sqlRowSet.getDate("birthday")).toLocalDate()).build();
        } else {
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

    private User makeUser( ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getLong("id"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .email(rs.getString("email"))
                .birthday(rs.getDate("birthday").toLocalDate()).build();
    }
}