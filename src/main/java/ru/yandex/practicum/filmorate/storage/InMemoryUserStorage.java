package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage{
    private long generatorId;
    private final Map<Long, User> users;

    public InMemoryUserStorage() {
        this.generatorId = 0;
        this.users = new HashMap<>();
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User crateUser(User newUser) {
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
            throw new NotFoundException(String.format("пользователя с id = %s не существует", id));
        }
        users.put(id, updatedUser);
        return updatedUser;
    }
}
