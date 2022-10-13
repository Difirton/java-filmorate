package ru.yandex.practicum.filmorate.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.repository.RecommendationRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RecommendationRepositoryJdbcImpl implements RecommendationRepository {
    private final JdbcOperations jdbcOperations;
    private static final String SQL_SELECT_MARKS_DIFFS =
            "SELECT f1.film_id AS id1, f2.film_id AS id2, f1.mark - f2.mark AS diff " +
            "FROM users_films_marks AS f1 " +
            "INNER JOIN users_films_marks AS f2 ON f1.user_id = f2.user_id";

    private static final String SQL_ALL_FILMS_RATED_WITH_USER_MARKS =
            "WITH all_films_rated AS (SELECT DISTINCT film_id FROM users_films_marks), " +
                    "user_marks AS (SELECT film_id, mark FROM users_films_marks WHERE user_id = ?) " +
                    "SELECT all_films_rated.film_id, user_marks.mark " +
                    "FROM all_films_rated " +
                    "LEFT JOIN user_marks ON all_films_rated.film_id = user_marks.film_id";

    @Override
    public List<Long> findRecommendationsByUser(Long userId, int count) {
        return this.recommendationsBasedOnMarks(userId, count);
    }

    @Override
    public List<Long> findRecommendationsByUser(Long userId) {
        return this.findRecommendationsByUser(userId, DEFAULT_RECOMMENDATIONS_COUNT);
    }

    private List<Long> recommendationsBasedOnMarks(Long userId, int count) {
        Map<Long, Map<Long, Double>> filmsMarkDiff = new HashMap<>();
        Map<Long, Map<Long, Integer>> filmsMarkWeight = new HashMap<>();

        SqlRowSet rs = jdbcOperations.queryForRowSet(SQL_SELECT_MARKS_DIFFS);
        this.collectDifferenceAndWeightMatrices(rs, filmsMarkDiff, filmsMarkWeight);
        System.out.println(String.valueOf('-').repeat(10));
        System.out.println(filmsMarkDiff);
        System.out.println(filmsMarkWeight);
        this.normalizeDifferenceMatrix(filmsMarkDiff, filmsMarkWeight);

        Map<Long, Integer> filmsRatedByUser = new HashMap<>();
        Map<Long, Double> filmsNotRatedByUser = new HashMap<>();

        this.collectUsersMarks(userId, filmsRatedByUser, filmsNotRatedByUser);
        this.predictFilmMarks(filmsRatedByUser, filmsMarkDiff, filmsNotRatedByUser, filmsMarkWeight);
        return filmsNotRatedByUser.entrySet()
                                  .stream()
                                  .filter(e -> e.getValue() > 5.0)
                                  .sorted((e1, e2) -> (int) (e1.getValue() - e2.getValue()))
                                  .map(Map.Entry::getKey)
                                  .collect(Collectors.toList());
    }

    private void collectDifferenceAndWeightMatrices(SqlRowSet rs, Map<Long, Map<Long, Double>> filmsMarkDiff,
                                                    Map<Long, Map<Long, Integer>> filmsMarkWeight ) {
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
    }

    private void normalizeDifferenceMatrix(Map<Long, Map<Long, Double>> filmsMarkDiff,
                                           Map<Long, Map<Long, Integer>> filmsMarkWeight ) {
        for (Long film1Id : filmsMarkDiff.keySet()) {
            for (Long film2Id : filmsMarkDiff.get(film1Id).keySet()) {
                double oldValue = filmsMarkDiff.get(film1Id).get(film2Id);
                int marksCount = filmsMarkWeight.get(film1Id).get(film2Id);
                filmsMarkDiff.get(film1Id).put(film2Id, oldValue / marksCount);
            }
        }
    }

    private void collectUsersMarks(Long userId, Map<Long, Integer> filmsRatedByUser,
                                   Map<Long, Double> filmsNotRatedByUser) {
        SqlRowSet rs = jdbcOperations.queryForRowSet(SQL_ALL_FILMS_RATED_WITH_USER_MARKS, userId);
        while (rs.next()) {
            Long filmId = rs.getLong("FILM_ID");
            int filmMark = rs.getInt("MARK");
            if (filmMark == 0) {
                filmsNotRatedByUser.put(filmId, (double) filmMark);
            } else {
                filmsRatedByUser.put(filmId, filmMark);
            }
        }
    }

    private void predictFilmMarks(Map<Long, Integer> filmsRatedByUser, Map<Long, Map<Long, Double>> filmsMarkDiff,
                                  Map<Long, Double> filmsNotRatedByUser, Map<Long, Map<Long, Integer>> filmsMarkWeight) {
        for (Long filmId : filmsNotRatedByUser.keySet()) {
            long rowMark = 0;
            long totalWeight = 0;
            for (Map.Entry<Long, Integer> markEntry : filmsRatedByUser.entrySet()) {
                long ratedFilmId = markEntry.getKey();
                int ratedFilmWeight = filmsMarkWeight.get(filmId).getOrDefault(ratedFilmId, 0);
                rowMark += (markEntry.getValue() + filmsMarkDiff.get(filmId).getOrDefault(ratedFilmId, 0.0)) * ratedFilmWeight;
                totalWeight += ratedFilmWeight;
            }
            double castTotalWeight = totalWeight;
            if (castTotalWeight == 0) {
                filmsNotRatedByUser.put(filmId, rowMark / Double.MIN_VALUE);
            } else {
                filmsNotRatedByUser.put(filmId, (double) rowMark / (double) totalWeight);
            }
        }
    }
}

