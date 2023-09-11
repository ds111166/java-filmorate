package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    @Qualifier("userDbStorage")
    private final UserStorage userStorage;
    @Qualifier("friendDbStorage")
    private final FriendStorage friendStorage;

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User createUser(User newUser) {
        return userStorage.createUser(newUser);
    }

    public User updateUser(User updatedUser) {
        return userStorage.updateUser(updatedUser);
    }

    public User getUserById(Long userId) {
        return userStorage.getUserById(userId);
    }

    public void addFriend(Long userId, Long friendId) {
        userStorage.getUserById(userId);
        userStorage.getUserById(friendId);
        friendStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        userStorage.getUserById(userId);
        userStorage.getUserById(friendId);
        friendStorage.deleteFriend(userId, friendId);
    }

    public List<User> getFriendsForUser(Long userId) {
        userStorage.getUserById(userId);
        return userStorage
                .getUsersByTheSpecifiedIds(friendStorage.getIdsFriendsForUser(userId));
    }

    public List<User> getMutualFriendsOfUsers(Long userId, long otherId) {
        userStorage.getUserById(userId);
        userStorage.getUserById(otherId);
        return userStorage
                .getUsersByTheSpecifiedIds(friendStorage.getMutualFriendsOfUsers(userId, otherId));
    }
}
