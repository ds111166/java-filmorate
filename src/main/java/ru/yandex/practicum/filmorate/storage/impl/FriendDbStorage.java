package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.storage.FriendStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashSet;
import java.util.Set;

@Component("friendDbStorage")
@RequiredArgsConstructor
public class FriendDbStorage implements FriendStorage {
    private final JdbcTemplate jdbcTemplate;


    @Override
    @Transactional
    public void addFriend(long userId, long friendId) {
        final String sqlUpdate = "UPDATE friendships SET direction=? WHERE user1_id=? AND user2_id=?;";
        final String sqlInsert = "INSERT INTO friendships (user1_id, user2_id, direction) VALUES(?, ?, ?);";
        long user1Id = Long.min(userId, friendId);
        long user2Id = Long.max(userId, friendId);

        final int direction = calculateDirection(user1Id, userId, friendId);

        final Friendship friendship = getFriendshipByIds(user1Id, user2Id);
        if (friendship == null) {
            jdbcTemplate.update(sqlInsert, user1Id, user2Id, direction);
        } else {
            int existingDirection = friendship.getDirection();
            if (existingDirection != 0 && existingDirection != direction) {
                jdbcTemplate.update(sqlUpdate, existingDirection + direction,
                        user1Id, user2Id);
            }
        }
    }


    @Override
    @Transactional
    public void deleteFriend(long userId, long friendId) {
        final String sqlUpdate = "UPDATE friendships SET direction=? WHERE user1_id=? AND user2_id=?;";
        final String sqlDelete = "DELETE FROM friendships where user1_id = ? and user2_id = ?;";
        long user1Id = Long.min(userId, friendId);
        long user2Id = Long.max(userId, friendId);
        final int direction = calculateDirection(user1Id, userId, friendId);
        final Friendship friendship = getFriendshipByIds(user1Id, user2Id);

        if (friendship != null) {
            int existingDirection = friendship.getDirection();
            if (existingDirection == 0) {
                jdbcTemplate.update(sqlUpdate, -direction, user1Id, user2Id);
            } else if (existingDirection == direction) {
                jdbcTemplate.update(sqlDelete, user1Id, user2Id);
            }
        }
    }

    @Override
    @Transactional
    public Set<Long> getIdsFriendsForUser(long userId) {
        final String sql = "SELECT user1_id FROM friendships where direction <= 0  and user2_id = ?\n" +
                "UNION\n" +
                "SELECT user2_id FROM friendships where direction >= 0  and user1_id = ?;";
        return new HashSet<>(jdbcTemplate.queryForList(sql,
                new Object[]{userId, userId}, new int[]{Types.BIGINT, Types.BIGINT}, Long.class));
    }

    @Override
    @Transactional
    public Set<Long> getMutualFriendsOfUsers(long userId, long otherId) {
        final String sql = "(SELECT user1_id FROM friendships where direction <= 0  and user2_id = ?\n" +
                "UNION\n" +
                "SELECT user2_id FROM friendships where direction >= 0  and user1_id = ?)\n" +
                "INTERSECT\n" +
                "(SELECT user1_id FROM friendships where direction <= 0  and user2_id = ?\n" +
                "UNION\n" +
                "SELECT user2_id FROM friendships where direction >= 0  and user1_id = ?);";
        return new HashSet<>(jdbcTemplate.queryForList(sql,
                new Object[]{userId, userId, otherId, otherId},
                new int[]{Types.BIGINT, Types.BIGINT, Types.BIGINT, Types.BIGINT},
                Long.class));
    }

    private Friendship getFriendshipByIds(long user1Id, long user2Id) {
        final String sqlSelect = "SELECT * FROM friendships where user1_id = ? and user2_id = ?;";
        try {
            return jdbcTemplate.queryForObject(sqlSelect, new Object[]{user1Id, user2Id},
                    new int[]{Types.BIGINT, Types.BIGINT}, (rs, rowNum) -> makeFriendship(rs));
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    private Friendship makeFriendship(ResultSet rs) throws SQLException {
        return Friendship.builder()
                .user1Id(rs.getLong("user1_id"))
                .user2Id(rs.getLong("user2_id"))
                .direction(rs.getInt("direction")).build();
    }

    private int calculateDirection(long user1Id, long userId, long friendId) {
        if (userId == friendId) {
            return 0;
        }
        if (user1Id == userId) {
            return 1;
        }
        return -1;
    }
}
