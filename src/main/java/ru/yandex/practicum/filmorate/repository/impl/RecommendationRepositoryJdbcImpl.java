package ru.yandex.practicum.filmorate.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.relational.core.sql.In;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.config.mapper.UserFilmMarkMapper;
import ru.yandex.practicum.filmorate.entity.Film;
import ru.yandex.practicum.filmorate.entity.Genre;
import ru.yandex.practicum.filmorate.entity.User;
import ru.yandex.practicum.filmorate.entity.UserFilmMark;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.RecommendationRepository;
import ru.yandex.practicum.filmorate.repository.UserFilmMarkRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;
import ru.yandex.practicum.filmorate.service.UserService;

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
    private final UserFilmMarkMapper userFilmMarkMapper;
    private final UserFilmMarkRepository userFilmMarkRepository;
    private final UserRepository userRepository;
    private final UserService userService;

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

    private final String SQL_SELECT_ALL_MARKS = "SELECT ufm.id, ufm.user_id, u.name, u.email, u.login, " +
            "u.birthday, ufm.film_id, f.name filmname, f.description, f.release_date, f.rate, ufm.mark  " +
            "FROM users_films_marks AS ufm INNER JOIN users AS u ON ufm.user_id = u.id " +
            "INNER JOIN films AS f ON ufm.film_id = f.id";

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
        Map<User, Map<Film, Integer>> data = this.getData();
        Map<Film, Map<Film, Double>> diff = new HashMap<>();
        Map<Film, Map<Film, Integer>> freq = new HashMap<>();
        HashMap<Film, Double> clean = new HashMap<>();

        for (Map<Film, Integer> userMarks : data.values()) {
            for (Map.Entry<Film, Integer> filmMark : userMarks.entrySet()) {
                Film film = filmMark.getKey();
                if (!diff.containsKey(film)) {
                    diff.put(film, new HashMap<>());
                    freq.put(film, new HashMap<>());
                }

                for (Map.Entry<Film, Integer> otherFilmMark : userMarks.entrySet()) {
                    Film otherFilm = otherFilmMark.getKey();

                    freq.get(film).put(
                            otherFilm,
                            freq.get(film).getOrDefault(otherFilm, 0) + 1);
                    diff.get(film).put(
                            otherFilm,
                            diff.get(film).getOrDefault(otherFilm, 0.0) + filmMark.getValue() - otherFilmMark.getValue());
                }
            }
        }

        for (Film film : diff.keySet()) {
            for (Film otherFilm : diff.get(film).keySet()) {
                double oldValue = diff.get(film).get(otherFilm);
                int marksCount = freq.get(film).get(otherFilm);
                diff.get(film).put(otherFilm, oldValue / marksCount);
            }
        }

        Map<Film, Integer> userMarks = data.get(userService.getUserById(userID));

        Map<Film, Double> uPred = new HashMap<>();
        Map<Film, Double> uFreq = new HashMap<>();

        for (Film dataFilm : userMarks.keySet()) {
            for (Film diffFilm : diff.keySet()) {
                double predictedValue =
                        diff.get(diffFilm).get(dataFilm) + userMarks.get(dataFilm).doubleValue();
                double finalValue = predictedValue * freq.get(diffFilm).get(dataFilm);
                uPred.put(diffFilm, uPred.getOrDefault(diffFilm, 0.0) + finalValue);
                uFreq.put(diffFilm, uFreq.getOrDefault(diffFilm, 0.0) + freq.get(diffFilm).get(dataFilm));
            }
        }

        for (Film film : uPred.keySet()) {
            if (uFreq.get(film) > 0) {
                clean.put(film, uPred.get(film) / uFreq.get(film).intValue());
            }
        }

        for (Film film : filmRepository.findAll()) {
            if (userMarks.containsKey(film)) {
                clean.put(film, userMarks.get(film).doubleValue());
            } else if (!clean.containsKey(film)) {
                clean.put(film, -1.0);
            }
        }

        return clean.entrySet().stream()
                    .filter(e->e.getValue() > 5.0)
                    .sorted((e1, e2) -> (int) (e1.getValue() - e2.getValue()))
                    .map(Map.Entry::getKey)
                    .map(Film::getId)
                    .collect(Collectors.toList());
    }

    private Map<User, Map<Film, Integer>> getData() {
        Map<User, Map<Film, Integer>> data = new HashMap<>();

        SqlRowSet rs = jdbcOperations.queryForRowSet(SQL_SELECT_ALL_MARKS);
        while (rs.next()) {
            User user = User.builder()
                            .login(rs.getString("login"))
                            .email(rs.getString("email"))
                            .build();
            Film film = Film.builder()
                    .name(rs.getString("FILMNAME"))
                    .description(rs.getString("description"))
                    .rate(rs.getDouble("rate"))
                    .build();

            data.putIfAbsent(user, new HashMap<>());
            data.get(user).put(film, rs.getInt("mark"));
        }

        return data;
    }
}

