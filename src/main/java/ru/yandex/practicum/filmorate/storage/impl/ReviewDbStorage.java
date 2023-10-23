package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.sql.*;
import java.util.List;
import java.util.Objects;


@Component
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public Review createReview(Review newReview) {
        final String sql = "INSERT INTO reviews (content, is_positive, user_id, film_id, useful)\n" +
                "VALUES(?, ?, ?, ?, ?);";
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        final PreparedStatementCreator preparedStatementCreator = connection -> {
            final PreparedStatement ps =
                    connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, newReview.getContent());
            ps.setBoolean(2, newReview.getIsPositive());
            ps.setLong(3, newReview.getUserId());
            ps.setLong(4, newReview.getFilmId());
            final Integer useful = newReview.getUseful();
            if(useful == null) {
                ps.setInt(5, 0);
            } else {
                ps.setInt(5, newReview.getUseful());
            }
            return ps;
        };
        jdbcTemplate.update(preparedStatementCreator, keyHolder);
        final int reviewId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        //newReview.setId(reviewId);
        final Review reviewById = getReviewById(reviewId);
        return reviewById;
    }

    @Override
    @Transactional
    public Review updateReview(Review updateReview) {
        final String sql = "UPDATE reviews SET content=?, is_positive=?, user_id=?, film_id=?, useful=? WHERE id=?;";
        int numberOfRecordsAffected = jdbcTemplate.update(sql, updateReview.getContent(), updateReview.getIsPositive(),
                updateReview.getUserId(), updateReview.getFilmId(), updateReview.getUseful(), updateReview.getReviewId());
        if (numberOfRecordsAffected == 0) {
            throw new NotFoundException(String.format("Пользователя с id = %s не существует", updateReview.getReviewId()));
        }
        return updateReview;
    }

    @Override
    @Transactional
    public void deleteReview(Integer reviewId) {
        final String sql = "DELETE FROM reviews WHERE id=?;";
        jdbcTemplate.update(sql, reviewId);
    }

    @Override
    @Transactional
    public Review getReviewById(Integer reviewId) {
        final String sql = "SELECT * FROM reviews WHERE id = ?;";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{reviewId},
                    new int[]{Types.BIGINT}, (rs, rowNum) -> makeReview(rs));
        } catch (EmptyResultDataAccessException ex) {
            throw new NotFoundException(String.format("Отзыва с id: %s не существует", reviewId));
        }
    }
    @Override
    @Transactional
    public List<Review> getReviews(Long filmId, Integer count) {
        final String sqlAll = "SELECT * FROM reviews LIMIT ?;";
        final String sqlByFilmId = "SELECT * FROM reviews WHERE film_id = ? LIMIT ?;";
        if(filmId == null) {
            return jdbcTemplate.query(sqlAll, new Object[]{count},
                    new int[]{Types.INTEGER}, (rs, rowNum) -> makeReview(rs));
        }
        return jdbcTemplate.query(sqlByFilmId, new Object[]{filmId, count},
                new int[]{Types.BIGINT, Types.INTEGER}, (rs, rowNum) -> makeReview(rs));
    }

    private Review makeReview(ResultSet rs) throws SQLException {
        return Review.builder()
                .reviewId(rs.getInt("id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .userId(rs.getLong("user_id"))
                .filmId(rs.getLong("film_id"))
                .useful(rs.getInt("useful"))
                .build();
    }
}
