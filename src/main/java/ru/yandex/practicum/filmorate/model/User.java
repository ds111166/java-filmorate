package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.Marker;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class User {
    @Null(groups = Marker.OnCreate.class)
    @NotNull(groups = Marker.OnUpdate.class)
    private Long id;
    private String name;
    @NotBlank(message = "электронная почта не может быть пустой")
    @Email(message = "электронная почта должна соответствовать принятым правлам")
    private String email;
    @NotEmpty
    @Pattern(regexp = "\\S+", message = "login не должен содержать пробельных символов")
    private String login;
    @Past(message = "Дата рождения должна быть ранее текущей даты")
    private LocalDate birthday;
    private final Set<Long> friends = new HashSet<>();
}
