package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FilmGenre {
    private Long id;
    private long filmId;
    private int genreId;
}
