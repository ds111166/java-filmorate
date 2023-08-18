package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.validation.Marker;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final FriendStorage friendStorage;

    public UserController(UserService userService, FriendStorage friendStorage) {
        this.userService = userService;
        this.friendStorage = friendStorage;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public User getUserById(@PathVariable("id") @Min(1) @NotNull Long userId) {
        return userService.getUserById(userId);
    }

    @GetMapping("/{id}/friends")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getFriendsForUser(@PathVariable("id") @Min(1) @NotNull Long id) {
        return userService.getFriendsForUser(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getFriendsCommonForUsers(@PathVariable("id") @Min(1) @NotNull Long id, @PathVariable("otherId") @Min(1) @NotNull long otherId) {
        return userService.getMutualFriendsOfUsers(id, otherId);
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@Valid @RequestBody User newUser) {
        final User user = userService.createUser(newUser);
        log.info("добавлен - {}!", user);
        return user;
    }

    @PutMapping
    @Validated({Marker.OnUpdate.class})
    @ResponseStatus(HttpStatus.OK)
    public User updateUser(@Valid @RequestBody User updatedUser) {
        final User user = userService.updateUser(updatedUser);
        log.info("обновлён - {}!", user);
        return user;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") @Min(1) @NotNull Long id, @PathVariable("friendId") @Min(1) @NotNull Long friendId) {
        log.info("пользователи с id: {} и id: {} стали друзьями!", id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") @Min(1) @NotNull Long id, @PathVariable("friendId") @Min(1) @NotNull Long friendId) {
        log.info("пользователи с id: {} и id: {} перестали дружить!", id, friendId);
        userService.deleteFriend(id, friendId);
    }
}
