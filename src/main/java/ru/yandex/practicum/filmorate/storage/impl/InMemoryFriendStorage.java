package ru.yandex.practicum.filmorate.storage.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component("inMemoryFriendStorage")
public class InMemoryFriendStorage implements FriendStorage {

    @Qualifier("inMemoryUserStorage")
    private final UserStorage userStorage;

    private final Map<Friendship, Integer> friendships = new HashMap<>();

    @Override
    public void addFriend(long userId, long friendId) {
        userStorage.getUserById(userId);
        userStorage.getUserById(friendId);
        long user1Id = Long.min(userId, friendId);
        long user2Id = Long.max(userId, friendId);
        final int direction = calculateDirection(user1Id, userId, friendId);
        final Friendship friendship = getFriendshipByIds(user1Id, user2Id);
        if (friendship == null) {
            friendships.put(Friendship.builder().user1Id(user1Id).user2Id(user2Id).build(),
                    direction);
        } else {
            int existingDirection = friendship.getDirection();
            if (existingDirection != 0 && existingDirection != direction) {
                friendships.put(Friendship.builder().user1Id(user1Id).user2Id(user2Id).build(),
                        existingDirection + direction);
            }
        }

    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        userStorage.getUserById(userId);
        userStorage.getUserById(friendId);
        long user1Id = Long.min(userId, friendId);
        long user2Id = Long.max(userId, friendId);
        final int direction = calculateDirection(user1Id, userId, friendId);
        final Friendship friendship = getFriendshipByIds(user1Id, user2Id);
        if (friendship != null) {
            int existingDirection = friendship.getDirection();
            if (existingDirection == 0) {
                friendships.put(Friendship.builder().user1Id(user1Id).user2Id(user2Id).build(),
                        -direction);
            } else if (existingDirection == direction) {
                friendships.remove(Friendship.builder().user1Id(user1Id).user2Id(user2Id).build());
            }
        }

    }

    @Override
    public Set<Long> getIdsFriendsForUser(long userId) {

        Set<Long> ids = new HashSet<>();
        for (Map.Entry<Friendship, Integer> entry : friendships.entrySet()) {
            final Friendship friendship = entry.getKey();
            final Integer direction = entry.getValue();
            if (friendship.getUser2Id() == userId && direction <= 0) {
                ids.add(friendship.getUser1Id());
            }
            if (friendship.getUser1Id() == userId && direction >= 0) {
                ids.add(friendship.getUser2Id());
            }
        }
        return ids;
    }

    @Override
    public Set<Long> getMutualFriendsOfUsers(long userId, long otherId) {

        return getIdsFriendsForUser(userId)
                .stream()
                .filter(id -> getIdsFriendsForUser(otherId).contains(id))
                .collect(Collectors.toSet());
    }

    private Friendship getFriendshipByIds(long user1Id, long user2Id) {
        Friendship friendship = Friendship.builder().user1Id(user1Id).user2Id(user2Id).build();
        if (friendships.containsKey(friendship)) {
            final Integer direction = friendships.get(friendship);
            friendship.setDirection(direction);
            return friendship;
        }
        return null;
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
