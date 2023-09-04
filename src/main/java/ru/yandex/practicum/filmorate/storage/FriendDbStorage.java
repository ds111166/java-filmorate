package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;

import java.util.Set;

@Component("friendDbStorage")
public class FriendDbStorage implements FriendStorage {
    @Override
    public void addFriend(long userId, long friendId) {

    }

    @Override
    public void deleteFriend(long userId, long friendId) {

    }

    @Override
    public Set<Long> getIdsFriendsForUser(long userId) {
        return null;
    }

    @Override
    public Set<Long> getMutualFriendsOfUsers(long userId, long otherId) {
        return null;
    }
}
