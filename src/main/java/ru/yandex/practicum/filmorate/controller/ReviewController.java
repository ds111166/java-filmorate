package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.validation.Marker;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @Validated({Marker.OnCreate.class})
    @ResponseStatus(HttpStatus.CREATED)
    public Review createReview(@Valid @RequestBody Review newReview) {
        log.info("Запрос на создание - {}", newReview);
        final Review createdReview = reviewService.createReview(newReview);
        log.info("Создан - {}", createdReview);
        return createdReview;
    }

    @PutMapping
    @Validated({Marker.OnUpdate.class})
    @ResponseStatus(HttpStatus.OK)
    public Review updateReview(@Valid @RequestBody Review updateReview) {
        log.info("Запрос на обновление - {}", updateReview);
        final Review updatedReview = reviewService.updateReview(updateReview);
        log.info("Обновлён - {}", updatedReview);
        return updatedReview;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteReview(@PathVariable("id") @NotNull Long id) {
        log.info("Запрос на удаление отзыва с id: {}", id);
        reviewService.deleteReview(id);
        log.info("Удален отзыв с id: {}", id);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Review getReviewById(@PathVariable("id") @NotNull Long reviewId) {
        log.info("Запрос на получение отзыва с id: {}", reviewId);
        final Review reviewById = reviewService.getReviewById(reviewId);
        log.info("Отправлен - {}", reviewById);
        return reviewById;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Review> getReviews(@RequestParam(value = "filmId", required = false) Long filmId,
                                   @RequestParam(value = "count", defaultValue = "10", required = false) Integer count) {
        log.info("Запрос получения отзывов для фильма с id: {}, количеством не более чем: {}", filmId, count);
        final List<Review> reviews = reviewService.getReviews(filmId, count);
        log.info("Количество отправленных отзывов равно: {}", reviews.size());
        return reviews;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") @NotNull Long id, @PathVariable("userId") @NotNull Long userId) {
        log.info("Запрос на 'лайк' отзыву с id: {} от пользователя с id: {}", id, userId);
        reviewService.addLike(id, userId);
        log.info("Лайк отзыву с id: {} от пользователя с id: {}", id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable("id") @NotNull Long id, @PathVariable("userId") @NotNull Long userId) {
        log.info("Запрос на 'дизлайк' отзыву с id: {} от пользователя с id: {}", id, userId);
        reviewService.addDislike(id, userId);
        log.info("Дизлайк отзыву с id: {} от пользователя с id: {}", id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") @NotNull Long id, @PathVariable("userId") @NotNull Long userId) {
        log.info("Запрос на удаление 'лайк' отзыву с id: {} от пользователя с id: {}", id, userId);
        reviewService.addDislike(id, userId);
        log.info("Удалён 'лайк' отзыву с id: {} от пользователя с id: {}", id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable("id") @NotNull Long id, @PathVariable("userId") @NotNull Long userId) {
        log.info("Запрос на удаление 'дизлайк' отзыву с id: {} от пользователя с id: {}", id, userId);
        reviewService.addLike(id, userId);
        log.info("Удалён 'дизлайк' отзыву с id: {} от пользователя с id: {}", id, userId);
    }
}
