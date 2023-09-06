package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.DateNotEarlier;
import ru.yandex.practicum.filmorate.validation.Marker;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class Film {
    @Null(groups = Marker.OnCreate.class)
    @NotNull(groups = Marker.OnUpdate.class)
    private Long id;
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    @Size(max = 200, message = "Длина описания превышает 200 символов!")
    private String description;
    @DateNotEarlier(message = "Дата релиза — не раньше 28 декабря 1895 года")
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной")
    private Integer duration;
    private Integer mpaId;
    @Builder.Default
    @NotNull
    private List<Integer> genreIds = new ArrayList<>();
    /*
    Из ТЗ: getFilmGenreIdsByFilmId - получить идентификаторы жанров фильма по идентификатору фильма
    При создании и получении фильмов передать список идентификаторов жанров и идентификатор рейтинга.
    Эти же данные должны передаваться при обновлении, создании и получении фильмов — обновить эти "эндпоинты".
     */
}
