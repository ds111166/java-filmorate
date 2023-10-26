package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.data.EventType;
import ru.yandex.practicum.filmorate.data.Operation;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    @Qualifier("userDbStorage")
    private final UserStorage userStorage;
    @Qualifier("friendDbStorage")
    private final FriendStorage friendStorage;
    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;
    private final EventService eventService;


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
        eventService.createEvent(userId, EventType.FRIEND, Operation.ADD, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        userStorage.getUserById(userId);
        userStorage.getUserById(friendId);
        friendStorage.deleteFriend(userId, friendId);
        eventService.createEvent(userId, EventType.FRIEND, Operation.REMOVE, friendId);
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

    public List<Film> getRecommendations(Long userId) {
        userStorage.getUserById(userId);
        return filmStorage.getRecommendationsForUser(userId);
    }

    public void deleteUser(Long userId) {
        userStorage.deleteUser(userId);
    }
}
