package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Component("inMemoryUserStorage")
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {

    private long generatorId = 0;
    private final Map<Long, User> users;

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public List<User> getUsersByTheSpecifiedIds(Set<Long> ids) {
        return users.values().stream()
                .filter(user -> ids.contains(user.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public User getUserById(Long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException(String.format("Пользователя с id = %s не существует", userId));
        }
        return users.get(userId);
    }

    @Override
    public User createUser(User newUser) {
        final long id = ++generatorId;
        newUser.setId(id);
        final String name = newUser.getName();
        if (name == null || name.isEmpty() || name.isBlank()) {
            newUser.setName(newUser.getLogin());
        }
        users.put(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public User updateUser(User updatedUser) {
        final long id = updatedUser.getId();
        if (!users.containsKey(id)) {
            throw new NotFoundException(String.format("Пользователя с id = %s не существует", id));
        }
        users.put(id, updatedUser);
        return updatedUser;
    }
}
