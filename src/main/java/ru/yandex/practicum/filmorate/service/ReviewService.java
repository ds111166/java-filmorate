package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.data.EventType;
import ru.yandex.practicum.filmorate.data.Operation;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

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
    private final EventService eventService;

    public Review createReview(Review newReview) {
        userStorage.getUserById(newReview.getUserId());
        filmStorage.getFilmById(newReview.getFilmId());
        final Review createdReview = reviewStorage.createReview(newReview);
        eventService.createEvent(createdReview.getUserId(),
                EventType.REVIEW, Operation.ADD, createdReview.getReviewId());
        return createdReview;
    }

    public Review updateReview(Review updateReviewData) {
        final Review updatedReview = reviewStorage.getReviewById(updateReviewData.getReviewId());
        final Boolean isPositive = updateReviewData.getIsPositive();
        if (isPositive != null) {
            updatedReview.setIsPositive(isPositive);
        }
        if (updateReviewData.getContent() != null) {
            updatedReview.setContent(updateReviewData.getContent());
        }
        final Review updateReview = reviewStorage.updateReview(updatedReview);
        eventService.createEvent(updateReview.getUserId(),
                EventType.REVIEW, Operation.UPDATE, updateReview.getReviewId());
        return updateReview;
    }

    public void deleteReview(Long reviewId) {
        final Review deletedReview = reviewStorage.getReviewById(reviewId);
        reviewStorage.deleteReview(reviewId);
        eventService.createEvent(deletedReview.getUserId(),
                EventType.REVIEW, Operation.REMOVE, deletedReview.getReviewId());
    }

    public Review getReviewById(Long reviewId) {
        return reviewStorage.getReviewById(reviewId);
    }

    public List<Review> getReviews(Long filmId, Integer count) {
        return reviewStorage.getReviews(filmId, count);
    }

    public void addLike(Long reviewId, Long userId) {
        changeUseful(reviewId, userId, LIKE);
    }

    public void addDislike(Long reviewId, Long userId) {
        changeUseful(reviewId, userId, DISLIKE);
    }

    private void changeUseful(Long reviewId, Long userId, int increment) {
        userStorage.getUserById(userId);
        reviewStorage.getReviewById(reviewId);
        reviewStorage.changeUseful(reviewId, increment);
    }
}
