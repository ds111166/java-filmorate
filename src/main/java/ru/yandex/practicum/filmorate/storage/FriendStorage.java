package ru.yandex.practicum.filmorate.storage;

import java.util.Set;

public interface FriendStorage {
    void addFriend(long userId, long friendId);

    void deleteFriend(long userId, long friendId);

    Set<Long> getIdsFriendsForUser(long userId);

    Set<Long> getMutualFriendsOfUsers(long userId, long otherId);

}
