package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.validation.Marker;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<User> getUsers() {
        log.info("Запрос на получение списка всех пользователей");
        final List<User> users = userService.getUsers();
        log.info("Отправлен список из {} пользователей", users.size());
        return users;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public User getUserById(@PathVariable("id") @NotNull Long userId) {
        log.info("Запрос на получение пользователя с id: {}", userId);
        final User userById = userService.getUserById(userId);
        log.info("Отправлен - {}", userById);
        return userById;
    }

    @GetMapping("/{id}/friends")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getFriendsForUser(@PathVariable("id") @NotNull Long id) {
        log.info("Запрос на получение друзей пользователя с id: {}", id);
        final List<User> friendsForUser = userService.getFriendsForUser(id);
        log.info("Отправлен список друзей из {} пользователй", friendsForUser.size());
        return friendsForUser;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getMutualFriendsOfUsers(@PathVariable("id") @NotNull Long id, @PathVariable("otherId") @NotNull long otherId) {
        log.info("Запрос на получение общих друзей пользователей с id: {} и {}", id, otherId);
        final List<User> mutualFriendsOfUsers = userService.getMutualFriendsOfUsers(id, otherId);
        log.info("Отправлен список друзей из {} пользователй", mutualFriendsOfUsers.size());
        return mutualFriendsOfUsers;
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@Valid @RequestBody User newUser) {
        log.info("Запрос на создание - {}", newUser);
        final User user = userService.createUser(newUser);
        log.info("Создан - {}", user);
        return user;
    }

    @PutMapping
    @Validated({Marker.OnUpdate.class})
    @ResponseStatus(HttpStatus.OK)
    public User updateUser(@Valid @RequestBody User updatedUser) {
        log.info("Запрос на обновление - {}", updatedUser);
        final User user = userService.updateUser(updatedUser);
        log.info("Обновлён - {}", user);
        return user;
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable("userId") @NotNull Long userId) {
        log.info("Запрос на удаление пользователя с id: {}", userId);
        userService.deleteUser(userId);
        log.info("Пользователь с id: {} удален", userId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void addFriend(@PathVariable("id") @NotNull Long id, @PathVariable("friendId") @NotNull Long friendId) {
        log.info("Запрос на дружбу пользователей с id: {} и {}", id, friendId);
        userService.addFriend(id, friendId);
        log.info("Пользователи с id: {} и id: {} стали друзьями", id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFriend(@PathVariable("id") @NotNull Long id, @PathVariable("friendId") @NotNull Long friendId) {
        log.info("Запрос на удаление дружбы пользователей с id: {} и {}", id, friendId);
        userService.deleteFriend(id, friendId);
        log.info("Пользователи с id: {} и id: {} перестали дружить", id, friendId);
    }

    @GetMapping("/{id}/feed")
    @ResponseStatus(HttpStatus.OK)
    public List<Event> getFeed(@PathVariable Long id) {
        return eventService.getFeed(id);
    }

    @GetMapping("/{id}/recommendations")
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getRecommendations(@PathVariable("id") Long id) {
        return userService.getRecommendations(id);
    }
}
