package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> getUsers();
    User createUser(User newUser);
    User updateUser(User updatedUser);
    User getUserById(Long userId);
    List<User> getUsersByTheSpecifiedIds(List<Long> ids);
}
