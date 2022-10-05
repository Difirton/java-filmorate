package ru.yandex.practicum.filmorate.config.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.entity.Review;
import ru.yandex.practicum.filmorate.entity.ReviewRate;
import ru.yandex.practicum.filmorate.entity.User;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ReviewRateRepositoryMapper implements RowMapper<ReviewRate> {

    @Override
    public ReviewRate mapRow(ResultSet rs, int rowNum) throws SQLException {
        return ReviewRate.builder()
                .user(User.builder()
                        .id(rs.getLong("user_id"))
                        .build())
                .review(Review.builder()
                        .id(rs.getLong("review_id"))
                        .build())
                .isPositive(rs.getBoolean("is_positive"))
                .build();
    }
}
