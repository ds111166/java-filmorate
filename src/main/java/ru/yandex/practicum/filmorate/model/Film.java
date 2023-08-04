package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.DateNotEarlier;
import ru.yandex.practicum.filmorate.validation.Marker;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
public class Film {
    public static final int FILM_DESCRIPTION_MAX_LENGTH = 200;
    @Null(groups = Marker.OnCreate.class)
    @NotNull(groups = Marker.OnUpdate.class)
    private Integer id;
    @NotEmpty(message = "название не может быть пустым")
    private String name;
    @Size(max = FILM_DESCRIPTION_MAX_LENGTH, message = "длина описания превышает 200 символов!")
    private String description;
    @DateNotEarlier(message = "дата релиза — не раньше 28 декабря 1895 года")
    private LocalDate releaseDate;
    @Positive(message = "продолжительность фильма должна быть положительной")
    private Integer duration;
}
