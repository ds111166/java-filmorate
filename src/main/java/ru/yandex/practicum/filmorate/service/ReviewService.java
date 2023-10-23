package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private static final int LIKE = 1;
    private static final int DISLIKE = -1;


    private final ReviewStorage reviewStorage;
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;
    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;

    public Review createReview(Review newReview) {
        userStorage.getUserById(newReview.getUserId());
        filmStorage.getFilmById(newReview.getFilmId());
        return reviewStorage.createReview(newReview);
    }

    public Review updateReview(Review updateReviewData) {
        final Review updatedReview = reviewStorage.getReviewById(updateReviewData.getReviewId());

        final Boolean isPositive = updateReviewData.getIsPositive();
        if(isPositive != null) {
            updatedReview.setIsPositive(isPositive);
        }
        if(updateReviewData.getContent()!=null) {
            updatedReview.setContent(updateReviewData.getContent());
        }
        return reviewStorage.updateReview(updatedReview);
    }

    public void deleteReview(Integer reviewId) {
        reviewStorage.deleteReview(reviewId);
    }

    public Review getReviewById(Integer reviewId) {
        return reviewStorage.getReviewById(reviewId);
    }

    public List<Review> getReviews(Long filmId, Integer count) {
        return reviewStorage.getReviews(filmId, count);
    }

    public void addLike(Integer reviewId, Long userId) {
        userStorage.getUserById(userId);
        reviewStorage.getReviewById(reviewId);
        reviewStorage.addLike(reviewId, userId);
    }

    public void addDislike(Integer reviewId, Long userId) {
        userStorage.getUserById(userId);
        reviewStorage.getReviewById(reviewId);
        reviewStorage.addDislike(reviewId, userId);
    }

    public void deleteLike(Integer reviewId, Long userId) {
        addDislike(reviewId, userId);
    }

    public void deleteDislike(Integer reviewId, Long userId) {
        addLike(reviewId, userId);
    }
}
