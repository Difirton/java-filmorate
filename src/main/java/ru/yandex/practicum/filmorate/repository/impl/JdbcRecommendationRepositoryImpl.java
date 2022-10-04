package ru.yandex.practicum.filmorate.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.repository.RecommendationRepository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JdbcRecommendationRepositoryImpl implements RecommendationRepository {
    private final JdbcOperations jdbcOperations;

    private static final String SQL_RECOMMENDATIONS_ON_LIKES = String.join(" ",
            "WITH COLIKERS AS (",
            "SELECT USERS_LIKES_FILMS.user_id, COLIKES.user_id AS coliker_id, COUNT(USERS_LIKES_FILMS.film_id) AS coliker_weight",
            "FROM USERS_LIKES_FILMS",
            "LEFT JOIN USERS_LIKES_FILMS COLIKES ON ",
            "USERS_LIKES_FILMS.film_id = COLIKES.film_id AND ",
            "USERS_LIKES_FILMS.user_id = ?1 AND ",
            "COLIKES.user_id <> ?1",
            "GROUP BY USERS_LIKES_FILMS.user_id, coliker_id)",

            "SELECT film_id",
            "FROM USERS_LIKES_FILMS",
            "JOIN COLIKERS ON USERS_LIKES_FILMS.user_id = COLIKERS.coliker_id",
            "AND COLIKERS.user_id = ?1",
            "AND USERS_LIKES_FILMS.film_id NOT IN (SELECT film_id FROM USERS_LIKES_FILMS WHERE user_id = ?1)",
            "GROUP BY film_id",
            "ORDER BY SUM(COLIKERS.coliker_weight) DESC",
            "LIMIT ?2");

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
