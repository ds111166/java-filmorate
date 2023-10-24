package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    Review createReview(Review newReview);

    Review updateReview(Review updateReview);

    void deleteReview(Long reviewId);

    Review getReviewById(Long reviewId);

    List<Review> getReviews(Long filmId, Integer count);

    void changeUseful(Long reviewId, int increment);
}
