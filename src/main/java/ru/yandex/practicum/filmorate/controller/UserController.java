package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;

import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class UserController {
    private int generatorId = 1;
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping("/users")
    public List<User> findAll() {
        //log.debug("Текущее количество постов: {} ", users.size());
        return new ArrayList<>(users.values());
    }
    @PostMapping(value = "/users")
    public ResponseEntity<User> create(@Valid @RequestBody User user)/* throws InvalidEmailException, UserAlreadyExistException */{
        /*if(user == null || user.getEmail() == null || user.getEmail().isBlank() || user.getEmail().isEmpty()){
            throw new InvalidEmailException("Invalid Email");
        }
        if(users.containsKey(user.getEmail())) {
            throw new UserAlreadyExistException("User Already Exist");
        }*/
        user.setId(generatorId++);
        return ResponseEntity.ok(user);
    }
    @PutMapping(value = "/users")
    public ResponseEntity<User> update(@Valid @RequestBody User user) /*throws InvalidEmailException*/ {
        /*if(user == null || user.getEmail() == null || user.getEmail().isBlank() || user.getEmail().isEmpty()){
            throw new InvalidEmailException("Invalid Email");
        }
        */
        users.put(user.getId(),user);
        return ResponseEntity.ok(user);
    }
}
