package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserStorage {
    List<User> getUsers();

    User createUser(User newUser);

    User updateUser(User updatedUser);

    User getUserById(Long userId);

    List<User> getUsersByTheSpecifiedIds(Set<Long> ids);
}
