package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.Marker;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
public class User {
    @Null(groups = Marker.OnCreate.class)
    @NotNull(groups = Marker.OnUpdate.class)
    private Integer id;
    private String name;
    @NotEmpty(message = "электронная почта не может быть пустой")
    @Email(message = "электронная почта должна соответствовать принятым правлам")
    private String email;
    @NotEmpty
    @Pattern(regexp = "\\S+", message = "login не должен содержать пробельных символов")
    private String login;
    @Past(message = "Дата рождения должна быть ранее текущей даты")
    private LocalDate birthday;
}
