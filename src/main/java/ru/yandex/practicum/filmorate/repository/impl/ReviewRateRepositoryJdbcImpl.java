package ru.yandex.practicum.filmorate.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.config.mapper.ReviewRateRepositoryMapper;
import ru.yandex.practicum.filmorate.entity.Review;
import ru.yandex.practicum.filmorate.entity.ReviewRate;
import ru.yandex.practicum.filmorate.entity.User;
import ru.yandex.practicum.filmorate.repository.ReviewRateRepository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReviewRateRepositoryJdbcImpl implements ReviewRateRepository {
    private final JdbcOperations jdbcOperations;
    private final ReviewRateRepositoryMapper reviewRateMapper;
    private static final String SQL_INSERT_ALL_FIELDS = "INSERT INTO users_rates_reviews (user_id, review_id, is_positive) " +
            "VALUES (?, ?, ?)";
    private static final String SQL_DELETE = "DELETE FROM users_rates_reviews " +
            "WHERE user_id = ? AND review_id = ? AND is_positive = ?";
    private static final String SQL_SELECT_BY_REVIEW_ID = "SELECT * FROM users_rates_reviews WHERE review_id = ?";
    private static final String SQL_UPDATE_REVIEW_USEFUL = "UPDATE reviews SET useful = useful + ? WHERE id = ?";


    @Override
    public ReviewRate save(User user, Review review, boolean isPositive) {
        this.jdbcOperations.update(SQL_INSERT_ALL_FIELDS, user.getId(), review.getId(), isPositive);
        this.jdbcOperations.update(SQL_UPDATE_REVIEW_USEFUL,
                (isPositive ? 1  : -1),
                review.getId());
        return ReviewRate.builder()
                .user(user)
                .review(review)
                .build();
    }

    @Override
    public int delete(Long userId, Long reviewId, boolean isPositive) {
        int result =  this.jdbcOperations.update(SQL_DELETE, userId, reviewId, isPositive);
        this.jdbcOperations.update(SQL_UPDATE_REVIEW_USEFUL,
                (isPositive ? -1  : 1),
                reviewId);
        return result;
    }

    @Override
    public List<ReviewRate> getByReviewId(Long reviewId) {
        return this.jdbcOperations.query(SQL_SELECT_BY_REVIEW_ID, reviewRateMapper, reviewId);
    }
}
