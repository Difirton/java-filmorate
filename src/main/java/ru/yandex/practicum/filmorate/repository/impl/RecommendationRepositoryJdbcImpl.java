package ru.yandex.practicum.filmorate.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.relational.core.sql.In;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.entity.Film;
import ru.yandex.practicum.filmorate.entity.Genre;
import ru.yandex.practicum.filmorate.entity.UserFilmMark;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.RecommendationRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RecommendationRepositoryJdbcImpl implements RecommendationRepository {
    private final JdbcOperations jdbcOperations;
    private final FilmRepository filmRepository;

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

    private static final String SQL_GET_MARKS_DATA = "SELECT film_id, user_id, mark FROM USERS_FILMS_MARKS";

    @Override
    public List<Long> findRecommendationsByUser(Long userID, int count) {
        //return this.recommendationsBasedOnLikes(userID, count);
        return this.recommendationsBasedOnMarks(userID, count);
    }

    @Override
    public List<Long> findRecommendationsByUser(Long userID) {
        return this.findRecommendationsByUser(userID, DEFAULT_RECOMMENDATIONS_COUNT);
    }

    private List<Long> recommendationsBasedOnLikes(Long userID, int count) {
        return jdbcOperations.queryForList(SQL_RECOMMENDATIONS_ON_LIKES, Long.class, userID, count);
    }

    private List<Long> recommendationsBasedOnMarks(Long userID, int count) {
        Map<Long, Map<Long, Integer>> data = this.getData();
        Map<Long, Map<Long, Double>> diff = new HashMap<>();
        Map<Long, Map<Long, Integer>> freq = new HashMap<>();
        HashMap<Long, Double> clean = new HashMap<>();

        for (Map<Long, Integer> userMarks : data.values()) {
            for (Map.Entry<Long, Integer> filmMark : userMarks.entrySet()) {
                long filmId = filmMark.getKey();
                if (!diff.containsKey(filmId)) {
                    diff.put(filmId, new HashMap<>());
                    freq.put(filmId, new HashMap<>());
                }

                for (Map.Entry<Long, Integer> otherFilmMark : userMarks.entrySet()) {
                    long otherFilmId = otherFilmMark.getKey();

                    freq.get(filmId).put(
                            otherFilmId,
                            freq.get(filmId).getOrDefault(otherFilmId, 0) + 1);
                    diff.get(filmId).put(
                            otherFilmId,
                            diff.get(filmId).getOrDefault(otherFilmId, 0.0) + filmMark.getValue() - otherFilmMark.getValue());
                }
            }
        }

        for (Long filmId : diff.keySet()) {
            for (Long otherFilmId : diff.get(filmId).keySet()) {
                double oldValue = diff.get(filmId).get(otherFilmId);
                int marksCount = freq.get(filmId).get(otherFilmId);
                diff.get(filmId).put(otherFilmId, oldValue / marksCount);
            }
        }


        for (Map.Entry<Long, Map<Long, Integer>> userMarks : data.entrySet()) {
            Map<Long, Double> uPred = new HashMap<>();
            Map<Long, Double> uFreq = new HashMap<>();
            for (Long dataFilmID : userMarks.getValue().keySet()) {
                for (Long diffFilmId : diff.keySet()) {
                    double predictedValue =
                            diff.get(diffFilmId).get(dataFilmID) + userMarks.getValue().get(dataFilmID).doubleValue();
                    double finalValue = predictedValue * freq.get(diffFilmId).get(dataFilmID);
                    uPred.put(diffFilmId, uPred.get(diffFilmId) + finalValue);
                    uFreq.put(diffFilmId, uFreq.get(diffFilmId) + freq.get(diffFilmId).get(dataFilmID));
                }
            }

            for (Long filmId : uPred.keySet()) {
                if (uFreq.get(filmId) > 0) {
                    clean.put(filmId, uPred.get(filmId) / uFreq.get(filmId).intValue());
                }
            }

            for (Long filmId : filmRepository.findAll().stream().map(Film::getId).collect(Collectors.toList())) {
                if (userMarks.getValue().containsKey(filmId)) {
                    clean.put(filmId, userMarks.getValue().get(filmId).doubleValue());
                } else if (!clean.containsKey(filmId)) {
                    clean.put(filmId, -1.0);
                }
            }
        }

        return clean.entrySet().stream()
                    .sorted((mark1, mark2) -> (int) (mark1.getValue() - mark2.getValue()))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
    }

    private Map<Long, Map<Long, Integer>> getData() {
        Map<Long, Map<Long, Integer>> data = new HashMap<>();
        data.put(1L, Map.of(
                1L, 4,
                2L, 6,
                4L, 8,
                5L, 3));

        data.put(2L, Map.of(
                1L, 7,
                2L, 4,
                3L, 3,
                4L, 4));

        data.put(3L, Map.of(
                2L, 1,
                3L, 9,
                4L, 2,
                5L, 3));

        data.put(4L, Map.of(
                1L, 2,
                2L, 8,
                3L, 6,
                5L, 5));

        data.put(5L, Map.of(
                1L, 1,
                3L, 4,
                4L, 8,
                5L, 9));


        return data;
    }
}

