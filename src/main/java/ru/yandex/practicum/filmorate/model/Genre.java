package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
public class Genre {
    private int id;
    @NotNull
    @Size(max = 100, message = "Длина наименования превышает 100 символов!")
    private String name;
}
