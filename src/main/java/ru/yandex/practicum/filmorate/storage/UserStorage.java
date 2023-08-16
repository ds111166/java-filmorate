package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> getUsers();
    User crateUser(User newUser);
    User updateUser(User updatedUser);
}
