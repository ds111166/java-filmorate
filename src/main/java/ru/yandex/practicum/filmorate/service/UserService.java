package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

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

    public void addFriend(Long id, Long friendId) {
        final User user = userStorage.getUserById(id);
        final User friendUser = userStorage.getUserById(friendId);
        user.getFriends().add(friendUser.getId());
    }

    public void deleteFriend(Long id, Long friendId) {
        final User user = userStorage.getUserById(id);
        final User friendUser = userStorage.getUserById(friendId);
        final Set<Long> friendsUser = user.getFriends();
        if(!friendsUser.contains(friendId) ){
            throw new NotFoundException(String.format("пользователь %s не является другом пользователя %s", friendUser, user));
        }
        friendsUser.remove(friendUser.getId());
    }
}
