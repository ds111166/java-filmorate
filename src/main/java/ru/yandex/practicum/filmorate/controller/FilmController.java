package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class FilmController {
    private int generatorId = 1;
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping("/films")
    public List<Film> findAll() {
        //log.debug("Текущее количество постов: {} ", users.size());
        return new ArrayList<>(films.values());
    }
    @PostMapping(value = "/films")
    public ResponseEntity<Film> add(@Valid @RequestBody Film film)/* throws InvalidEmailException, UserAlreadyExistException */{
        /*if(user == null || user.getEmail() == null || user.getEmail().isBlank() || user.getEmail().isEmpty()){
            throw new InvalidEmailException("Invalid Email");
        }
        if(users.containsKey(user.getEmail())) {
            throw new UserAlreadyExistException("User Already Exist");
        }*/
        film.setId(generatorId++);
        return ResponseEntity.ok(film);
    }
    @PutMapping(value = "/films")
    public ResponseEntity<Film> update(@Valid @RequestBody Film film) /*throws InvalidEmailException*/ {
        /*if(user == null || user.getEmail() == null || user.getEmail().isBlank() || user.getEmail().isEmpty()){
            throw new InvalidEmailException("Invalid Email");
        }
        */
        films.put(film.getId(),film);
        return ResponseEntity.ok(film);
    }
}
