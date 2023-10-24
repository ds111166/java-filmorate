package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.Marker;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Data
@Builder
public class Review {
    @Null(groups = Marker.OnCreate.class)
    @NotNull(groups = Marker.OnUpdate.class)
    private Long reviewId;
    @NotNull(groups = Marker.OnCreate.class, message = "Контент отзыва не должен быть пустым")
    private String content;
    @NotNull(groups = Marker.OnCreate.class)
    private Boolean isPositive;
    @NotNull(groups = Marker.OnCreate.class)
    private Long userId;
    @NotNull(groups = Marker.OnCreate.class)
    private Long filmId;
    private Integer useful;
}
