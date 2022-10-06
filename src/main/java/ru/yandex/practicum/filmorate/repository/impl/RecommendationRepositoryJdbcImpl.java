package ru.yandex.practicum.filmorate.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.repository.RecommendationRepository;

import java.util.List;
import java.util.StringJoiner;

@Repository
@RequiredArgsConstructor
public class RecommendationRepositoryJdbcImpl implements RecommendationRepository {
    private final JdbcOperations jdbcOperations;

    private static final String SQL_RECOMMENDATIONS_ON_LIKES = new StringJoiner(" ")
            .add("WITH COLIKERS AS (")
            .add("SELECT USERS_LIKES_FILMS.user_id, COLIKES.user_id AS coliker_id, COUNT(USERS_LIKES_FILMS.film_id) AS coliker_weight")
            .add("FROM USERS_LIKES_FILMS")
            .add("LEFT JOIN USERS_LIKES_FILMS COLIKES ON")
            .add("USERS_LIKES_FILMS.film_id = COLIKES.film_id AND")
            .add("USERS_LIKES_FILMS.user_id = ?1 AND")
            .add("COLIKES.user_id <> ?1")
            .add("GROUP BY USERS_LIKES_FILMS.user_id, coliker_id)")

            .add("SELECT film_id")
            .add("FROM USERS_LIKES_FILMS")
            .add("JOIN COLIKERS ON USERS_LIKES_FILMS.user_id = COLIKERS.coliker_id")
            .add("AND COLIKERS.user_id = ?1")
            .add("AND USERS_LIKES_FILMS.film_id NOT IN (SELECT film_id FROM USERS_LIKES_FILMS WHERE user_id = ?1)")
            .add("GROUP BY film_id")
            .add("ORDER BY SUM(COLIKERS.coliker_weight) DESC")
            .add("LIMIT ?2")
            .toString();

    @Override
    public List<Long> findRecommendationsByUser(Long userID, int count) {
        return this.recommendationsBasedOnLikes(userID, count);
    }

    @Override
    public List<Long> findRecommendationsByUser(Long userID) {
        return this.findRecommendationsByUser(userID, DEFAULT_RECOMMENDATIONS_COUNT);
    }

    private List<Long> recommendationsBasedOnLikes(Long userID, int count) {
        return jdbcOperations.queryForList(SQL_RECOMMENDATIONS_ON_LIKES, Long.class, userID, count);
    }
}
