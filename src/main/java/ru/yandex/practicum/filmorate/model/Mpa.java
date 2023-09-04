package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Mpa {
    private int id;
    @NotNull
    @Size(max = 100, message = "Длина наименования превышает 100 символов!")
    private String name;
}
