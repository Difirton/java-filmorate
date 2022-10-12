package ru.yandex.practicum.filmorate.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.repository.RecommendationRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

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

    private final String SQL_SELECT_MARKS_DIFFS =
            "SELECT f1.film_id AS id1, f2.film_id AS id2, f1.mark - f2.mark AS diff " +
            "FROM users_films_marks AS f1 " +
            "INNER JOIN users_films_marks AS f2 ON f1.user_id = f2.user_id";

    private final String SQL_ALL_FILMS_RATED_WITH_USER_MARKS =
            "WITH all_films_rated AS (SELECT DISTINCT film_id FROM users_films_marks), " +
                    "user_marks AS (SELECT film_id, mark FROM users_films_marks WHERE user_id = ?) " +
                    "SELECT all_films_rated.film_id, user_marks.mark " +
                    "FROM all_films_rated " +
                    "LEFT JOIN user_marks ON all_films_rated.film_id = user_marks.film_id";

    @Override
    public List<Long> findRecommendationsByUser(Long userId, int count) {
        //return this.recommendationsBasedOnLikes(userId, count);
        return this.recommendationsBasedOnMarks(userId, count);
    }

    @Override
    public List<Long> findRecommendationsByUser(Long userId) {
        return this.findRecommendationsByUser(userId, DEFAULT_RECOMMENDATIONS_COUNT);
    }

    private List<Long> recommendationsBasedOnLikes(Long userId, int count) {
        return jdbcOperations.queryForList(SQL_RECOMMENDATIONS_ON_LIKES, Long.class, userId, count);
    }

    private List<Long> recommendationsBasedOnMarks(Long userId, int count) {
        Map<Long, Map<Long, Double>> filmsMarkDiff = new HashMap<>();
        Map<Long, Map<Long, Integer>> filmsMarkWeight = new HashMap<>();

        SqlRowSet rs = jdbcOperations.queryForRowSet(SQL_SELECT_MARKS_DIFFS);
        System.out.println(SQL_SELECT_MARKS_DIFFS);

        // collect difference and weight matrices
        while (rs.next()) {
            long film1Id = rs.getLong("ID1");
            long film2Id = rs.getLong("ID2");
            int dif1to2 = rs.getInt("DIFF");

            if (!filmsMarkDiff.containsKey(film1Id)) {
                filmsMarkDiff.put(film1Id, new HashMap<>());
                filmsMarkWeight.put(film1Id, new HashMap<>());
            }

            filmsMarkDiff.get(film1Id).put(
                    film2Id,
                    filmsMarkDiff.get(film1Id).getOrDefault(film2Id, 0.0) + dif1to2);
            filmsMarkWeight.get(film1Id).put(
                    film2Id,
                    filmsMarkWeight.get(film1Id).getOrDefault(film2Id, 0) + 1);
        }

        // normalize difference matrix
        for (Long film1Id : filmsMarkDiff.keySet()) {
            for (Long film2Id : filmsMarkDiff.get(film1Id).keySet()) {
                double oldValue = filmsMarkDiff.get(film1Id).get(film2Id);
                int marksCount = filmsMarkWeight.get(film1Id).get(film2Id);
                filmsMarkDiff.get(film1Id).put(film2Id, oldValue / marksCount);
            }
        }

        Map<Long, Integer> filmsRatedByUser = new HashMap<>();
        Map<Long, Double> filmsNotRatedByUser = new HashMap<>();

        // collect users marks
        rs = jdbcOperations.queryForRowSet(SQL_ALL_FILMS_RATED_WITH_USER_MARKS, userId);
        while (rs.next()) {
            Long filmId = rs.getLong("FILM_ID");
            int filmMark = rs.getInt("MARK");
            if (filmMark == 0) {
                filmsNotRatedByUser.put(filmId, (double) filmMark);
            } else {
                filmsRatedByUser.put(filmId, filmMark);
            }
        }

        // predict film marks
        for (Long filmId : filmsNotRatedByUser.keySet()) {
            long rowMark = 0;
            long totalWeight = 0;
            for (Map.Entry<Long, Integer> markEntry : filmsRatedByUser.entrySet()) {
                long ratedFilmId = markEntry.getKey();
                int ratedFilmWeight = filmsMarkWeight.get(filmId).get(ratedFilmId);
                rowMark += (markEntry.getValue() + filmsMarkDiff.get(filmId).get(ratedFilmId)) * ratedFilmWeight;
                totalWeight += ratedFilmWeight;
            }
            filmsNotRatedByUser.put(filmId, (double) rowMark / (double) totalWeight);
        }

        return filmsNotRatedByUser.entrySet()
                                  .stream()
                                  .filter(e -> e.getValue() > 5.0)
                                  .sorted((e1, e2) -> (int) (e1.getValue() - e2.getValue()))
                                  .map(Map.Entry::getKey)
                                  .collect(Collectors.toList());
    }
}

