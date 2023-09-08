package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashSet;
import java.util.Set;

@Component("friendDbStorage")
@RequiredArgsConstructor
public class FriendDbStorage implements FriendStorage {
    private final JdbcTemplate jdbcTemplate;
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    @Override
    @Transactional
    public void addFriend(long userId, long friendId) {
        userStorage.getUserById(userId);
        userStorage.getUserById(friendId);
        final String sqlUpdate = "UPDATE friendships SET difference=? WHERE user_id=? AND friend_id=?;";
        final String sqlInsert = "INSERT INTO friendships (user_id, friend_id, difference) VALUES(?, ?, ?);";
        final long difference = userId - friendId;

        final Friendship pair2to1 = getFriendshipByIds(friendId, userId);
        if (pair2to1 != null) {
            if (pair2to1.getDifference() != 0) {
                jdbcTemplate.update(sqlUpdate, pair2to1.getDifference() + difference,
                        pair2to1.getUserId(), pair2to1.getFriendId());
            }
        } else {
            try {
                jdbcTemplate.update(sqlInsert, userId, friendId, difference);
            } catch (DuplicateKeyException ignored) {
            }
        }
    }

    @Override
    @Transactional
    public void deleteFriend(long userId, long friendId) {
        userStorage.getUserById(userId);
        userStorage.getUserById(friendId);
        final String sqlUpdate = "UPDATE friendships SET difference=? WHERE user_id=? AND friend_id=?;";
        final String sqlDelete = "DELETE FROM friendships where user_id = ? and friend_id = ?;";
        final long difference = userId - friendId;
        final Friendship pair1to2 = getFriendshipByIds(userId, friendId);
        final Friendship pair2to1 = getFriendshipByIds(friendId, userId);
        final boolean is1to2 = pair1to2 != null;
        final boolean is2to1 = pair2to1 != null;
        if (difference == 0) {
            if (is1to2) {
                jdbcTemplate.update(sqlDelete, pair1to2.getUserId(), pair1to2.getFriendId());
            }
            if (is2to1) {
                jdbcTemplate.update(sqlDelete, pair2to1.getUserId(), pair2to1.getFriendId());
            }
            return;
        }
        if (is1to2) {
            if (pair1to2.getDifference() == 0) {
                jdbcTemplate.update(sqlUpdate, difference, pair1to2.getUserId(), pair1to2.getFriendId());
            } else {
                jdbcTemplate.update(sqlDelete, pair1to2.getUserId(), pair1to2.getFriendId());
            }
        }
        if (is2to1) {
            if (pair2to1.getDifference() == 0) {
                jdbcTemplate.update(sqlUpdate, -1 * difference, pair2to1.getUserId(), pair2to1.getFriendId());
            }
        }
    }

    @Override
    @Transactional
    public Set<Long> getIdsFriendsForUser(long userId) {
        userStorage.getUserById(userId);
        final String sql = "SELECT user_id FROM friendships where difference = 0 and friend_id = ?\n" +
                "union\n" +
                "SELECT friend_id FROM friendships where user_id = ?;";
        return new HashSet<>(jdbcTemplate.queryForList(sql,
                new Object[]{userId, userId}, new int[]{Types.BIGINT, Types.BIGINT}, Long.class));
    }

    @Override
    @Transactional
    public Set<Long> getMutualFriendsOfUsers(long userId, long otherId) {
        userStorage.getUserById(userId);
        userStorage.getUserById(otherId);
        final String sql = "(SELECT user_id FROM friendships where difference = 0 and friend_id = ?\n" +
                "union \n" +
                "SELECT friend_id FROM friendships where user_id = ?)\n" +
                "intersect\n" +
                "(SELECT user_id FROM friendships where difference = 0 and friend_id = ?\n" +
                "union \n" +
                "SELECT friend_id FROM friendships where user_id = ?);";
        return new HashSet<>(jdbcTemplate.queryForList(sql,
                new Object[]{userId, userId, otherId, otherId},
                new int[]{Types.BIGINT, Types.BIGINT, Types.BIGINT, Types.BIGINT},
                Long.class));
    }

    private Friendship makeFriendship(ResultSet rs) throws SQLException {
        return Friendship.builder()
                .userId(rs.getLong("user_id"))
                .friendId(rs.getLong("friend_id"))
                .difference(rs.getLong("difference")).build();
    }

    private Friendship getFriendshipByIds(long friendId, long userId) {
        final String sqlSelect = "SELECT * FROM friendships where user_id = ? and friend_id = ?;";
        try {
            return jdbcTemplate.queryForObject(sqlSelect, new Object[]{friendId, userId},
                    new int[]{Types.BIGINT, Types.BIGINT}, (rs, rowNum) -> makeFriendship(rs));
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }
}
