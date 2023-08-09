package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.Marker;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping("/users")
public class UserController {
    private long generatorId = 0;
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public ResponseEntity<User> createUser(@Valid @RequestBody User newUser) {
        final long id = ++generatorId;
        newUser.setId(id);
        final String name = newUser.getName();
        if (name == null || name.isEmpty() || name.isBlank()) {
            newUser.setName(newUser.getLogin());
        }
        users.put(newUser.getId(), newUser);
        log.info("добавлен - {}!", newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @PutMapping
    @Validated({Marker.OnUpdate.class})
    public ResponseEntity<User> updateUser(@Valid @RequestBody User updateUser) throws NotFoundException {
        final long id = updateUser.getId();
        if (!users.containsKey(id)) {
            throw new NotFoundException(String.format("пользователя с id = %s не существует", id));
        }
        users.put(id, updateUser);
        log.info("обновлён - {}!", updateUser);
        return ResponseEntity.ok(updateUser);
    }


}
