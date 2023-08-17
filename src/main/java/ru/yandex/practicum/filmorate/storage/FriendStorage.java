package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendStorage {
    void addFriend(Long userId, Long friendId);
    void deleteFriend(Long userId, Long friendId);
    List<Long> getFriendsForUser(Long userId);
    List<Long> getFriendsSharedForUsers(Long id, Long otherId);

}
