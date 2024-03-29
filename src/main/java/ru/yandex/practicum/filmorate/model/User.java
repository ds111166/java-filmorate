package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.Marker;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Builder
public class User {
    @Null(groups = Marker.OnCreate.class)
    @NotNull(groups = Marker.OnUpdate.class)
    private Long id;
    @NotNull
    @NotEmpty
    @Pattern(regexp = "\\S+", message = "login не должен содержать пробельных символов")
    private String login;
    private String name;
    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Электронная почта должна соответствовать принятым правилам")
    private String email;
    @NotNull
    @Past(message = "Дата рождения должна быть ранее текущей даты")
    private LocalDate birthday;
}
