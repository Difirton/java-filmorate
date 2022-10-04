package ru.yandex.practicum.filmorate.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.config.mapper.ReviewRepositoryMapper;
import ru.yandex.practicum.filmorate.entity.Review;
import ru.yandex.practicum.filmorate.entity.User;
import ru.yandex.practicum.filmorate.repository.ReviewRepository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryJdbcImpl implements ReviewRepository {
    private final JdbcOperations jdbcOperations;
    private final ReviewRepositoryMapper reviewMapper;
    private static final String SQL_INSERT_ALL_FIELDS = "INSERT INTO reviews (content, is_positive, " +
            "user_id, film_id, useful) VALUES (?,?,?,?,?)";
    private static final String SQL_UPDATE_ALL_FIELDS = "UPDATE reviews SET content = ?, is_positive = ? WHERE id = ?";
    private static final String SQL_DELETE_BY_ID = "DELETE FROM reviews WHERE id = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM reviews ORDER BY useful DESC";
    private static final String SQL_SELECT_ALL_COUNT = "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM reviews WHERE id = ?";
    private static final String SQL_SELECT_BY_FILM_ID = "SELECT * FROM reviews WHERE film_id = ? " +
            "ORDER BY useful DESC LIMIT ?";
    private static final String SQL_SELECT_RATES_BY_REVIEW_ID = "SELECT * FROM users_rates_reviews WHERE review_id = ?";

    @Override
    public Review save(Review review) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        this.jdbcOperations.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(SQL_INSERT_ALL_FIELDS, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.getIsPositive());
            ps.setLong(3, review.getUserId());
            ps.setLong(4, review.getFilmId());
            ps.setInt(5, review.getUseful());
            return ps;
        }, keyHolder);
        review.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return review;
    }

    @Override
    public Review update(Review review) {
        jdbcOperations.update(SQL_UPDATE_ALL_FIELDS,
                review.getContent(),
                review.getIsPositive(),
                review.getId());
        return review;
    }

    @Override
    public int deleteById(Long id) {
        return jdbcOperations.update(SQL_DELETE_BY_ID, id);
    }

    @Override
    public List<Review> findAll() {
        return jdbcOperations.query(SQL_SELECT_ALL, reviewMapper);
    }

    @Override
    public List<Review> findAll(Integer count) {
        return jdbcOperations.query(SQL_SELECT_ALL_COUNT, reviewMapper, count);
    }

    @Override
    public Optional<Review> findById(Long id) {
        Review review = jdbcOperations.queryForObject(SQL_SELECT_BY_ID, reviewMapper, id);
        if (review != null) {
            review.setUsersRates(this.jdbcOperations.query(SQL_SELECT_RATES_BY_REVIEW_ID,
                    (rs, rowNum) -> User.builder()
                            .id(rs.getLong("user_id"))
                            .build(), id));
            return Optional.of(review);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public int[] saveAll(List<Review> reviews) {
        return this.jdbcOperations.batchUpdate(SQL_INSERT_ALL_FIELDS,
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                        preparedStatement.setString(1, reviews.get(i).getContent());
                        preparedStatement.setBoolean(2, reviews.get(i).getIsPositive());
                        preparedStatement.setLong(3, reviews.get(i).getUserId());
                        preparedStatement.setLong(4, reviews.get(i).getFilmId());
                        preparedStatement.setInt(5, reviews.get(i).getUseful());
                    }
                    public int getBatchSize() {
                        return reviews.size();
                    }
                });
    }

    @Override
    public List<Review> findReviewsByFilmId(Long filmId, Integer count) {
        return jdbcOperations.query(SQL_SELECT_BY_FILM_ID, reviewMapper, filmId, count);
    }
}
