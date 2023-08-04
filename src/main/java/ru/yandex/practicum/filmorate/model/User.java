package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;
import ru.yandex.practicum.filmorate.validation.Marker;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class User {
    @Null(groups = Marker.OnCreate.class)
    @NotNull(groups = Marker.OnUpdate.class)
    private Integer id;
    private String name;
    @NonNull
    @NotBlank
    @NotEmpty(message = "элестронная почта не может быть пустой")
    @Email(message = "элестронная почта должна соответствовать принятым правлам")
    private String email;

    @NonNull
    @NotBlank
    @NotEmpty
    @Pattern(regexp = "\\S+", message = "login не должен содержать прбельных символов")
    private String login;
    @Past(message = "Дата рождения должна быть ранее текущей даты")
    private LocalDate birthday;
}
