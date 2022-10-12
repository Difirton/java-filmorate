package ru.yandex.practicum.filmorate.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.config.mapper.UserFilmMarkMapper;
import ru.yandex.practicum.filmorate.entity.UserFilmMark;
import ru.yandex.practicum.filmorate.repository.UserFilmMarkRepository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserFilmMarkRepositoryJdbcImpl implements UserFilmMarkRepository {
    private final JdbcOperations jdbcOperations;
    private final UserFilmMarkMapper userFilmMarkMapper;
    private final String SQL_INSERT_ALL_FIELDS = "INSERT INTO users_films_marks (user_id, film_id, mark) VALUES (?, ?, ?)";
    private final String SQL_UPDATE_ALL_FIELDS = "UPDATE users_films_marks SET user_id = ?, film_id = ?, mark = ? WHERE id = ?";
    private final String SQL_DELETE_BY_ID = "DELETE FROM users_films_marks WHERE id = ?";
    private final String SQL_SELECT_ALL = "SELECT ufm.id, ufm.user_id, u.name, u.email, u.login, " +
            "u.birthday, ufm.film_id, f.name, f.description, f.duration, f.release_date, f.rate, ufm.mark  " +
            "FROM users_films_marks AS ufm INNER JOIN users AS u ON ufm.user_id = u.id " +
            "INNER JOIN films AS f ON ufm.film_id = f.id ORDER BY ufm.id";
    private final String SQL_SELECT_BY_ID = "SELECT ufm.id, ufm.user_id, u.name, u.email, u.login, " +
            "u.birthday, ufm.film_id, f.name, f.description, f.duration, f.release_date, f.rate, ufm.mark  " +
            "FROM users_films_marks AS ufm INNER JOIN users AS u ON ufm.user_id = u.id " +
            "INNER JOIN films AS f ON ufm.film_id = f.id WHERE ufm.id = ?";
    private static final String SQL_SELECT_USER_MARK_BY_FILM_AND_USER_IDS = "SELECT ufm.id, ufm.user_id, u.name, u.email, u.login, " +
            "u.birthday, ufm.film_id, f.name, f.description, f.duration, f.release_date, f.rate, ufm.mark  " +
            "FROM users_films_marks AS ufm INNER JOIN users AS u ON ufm.user_id = u.id " +
            "INNER JOIN films AS f ON ufm.film_id = f.id WHERE film_id = ? AND user_id = ?";

    @Override
    public Optional<UserFilmMark> findByUserIdAndFilmId(Long userId, Long filmId) {
        return Optional.ofNullable(jdbcOperations.queryForObject(SQL_SELECT_USER_MARK_BY_FILM_AND_USER_IDS, userFilmMarkMapper, filmId, userId));
    }

    @Override
    public UserFilmMark save(UserFilmMark userFilmMark) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        this.jdbcOperations.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(SQL_INSERT_ALL_FIELDS, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, userFilmMark.getUser().getId());
            ps.setLong(2, userFilmMark.getFilm().getId());
            ps.setInt(3, userFilmMark.getMark());
            return ps;
        }, keyHolder);
        userFilmMark.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return userFilmMark;
    }

    @Override
    public UserFilmMark update(UserFilmMark userFilmMark) {
        jdbcOperations.update(SQL_UPDATE_ALL_FIELDS,
                userFilmMark.getUser().getId(),
                userFilmMark.getFilm().getId(),
                userFilmMark.getMark(),
                userFilmMark.getId());
        return userFilmMark;
    }

    @Override
    public int deleteById(Long id) {
        return this.jdbcOperations.update(SQL_DELETE_BY_ID, id);
    }

    @Override
    public List<UserFilmMark> findAll() {
        return this.jdbcOperations.query(SQL_SELECT_ALL, userFilmMarkMapper);
    }

    @Override
    public Optional<UserFilmMark> findById(Long id) {
        return Optional.ofNullable(jdbcOperations.queryForObject(SQL_SELECT_BY_ID, userFilmMarkMapper, id));
    }

    @Override
    public int[] saveAll(List<UserFilmMark> userFilmMarks) {
        return this.jdbcOperations.batchUpdate(SQL_INSERT_ALL_FIELDS,
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                        preparedStatement.setLong(1, userFilmMarks.get(i).getUser().getId());
                        preparedStatement.setLong(2, userFilmMarks.get(i).getFilm().getId());
                        preparedStatement.setInt(3, userFilmMarks.get(i).getMark());
                    }
                    public int getBatchSize() {
                        return userFilmMarks.size();
                    }
                });
    }
}
