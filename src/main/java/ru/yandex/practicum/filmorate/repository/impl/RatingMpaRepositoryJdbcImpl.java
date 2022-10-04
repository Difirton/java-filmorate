package ru.yandex.practicum.filmorate.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.entity.RatingMPA;
import ru.yandex.practicum.filmorate.repository.RatingMpaRepository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RatingMpaRepositoryJdbcImpl implements RatingMpaRepository {
    private final JdbcOperations jdbcOperations;
    private static final String SQL_INSERT_TITLE = "INSERT INTO rating_mpa (title) VALUES (?)";
    private static final String SQL_UPDATE_TITLE = "UPDATE rating_mpa SET title = ? WHERE id = ?";
    private static final String SQL_DELETE_BY_ID = "DELETE FROM rating_mpa WHERE id = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM rating_mpa ORDER BY id";
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM rating_mpa WHERE id = ?";

    @Override
    public RatingMPA save(RatingMPA ratingMPA) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        this.jdbcOperations.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(SQL_INSERT_TITLE, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, ratingMPA.getTitle());
                    return ps;
                    }, keyHolder);
        ratingMPA.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return ratingMPA;
    }

    @Override
    public RatingMPA update(RatingMPA ratingMPA) {
        this.jdbcOperations.update(SQL_UPDATE_TITLE, ratingMPA.getTitle(), ratingMPA.getId());
        return ratingMPA;
    }

    @Override
    public int deleteById(Long id) {
        return jdbcOperations.update(SQL_DELETE_BY_ID, id);
    }

    @Override
    public List<RatingMPA> findAll() {
        return this.jdbcOperations.query(SQL_SELECT_ALL,
                (resultSet, rowNum) -> RatingMPA.builder()
                        .id(resultSet.getLong("id"))
                        .title(resultSet.getString("title")).build());
    }

    @Override
    public Optional<RatingMPA> findById(Long id) {
        return this.jdbcOperations.queryForObject(SQL_SELECT_BY_ID,
                (resultSet, rowNum) -> Optional.of(RatingMPA.builder()
                        .id(resultSet.getLong("id"))
                        .title(resultSet.getString("title")).build()), id);
    }

    @Override
    public int[] saveAll(List<RatingMPA> ratingsMPA) {
        return this.jdbcOperations.batchUpdate(SQL_INSERT_TITLE,
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                        preparedStatement.setString(1, ratingsMPA.get(i).getTitle());
                    }
                    public int getBatchSize() {
                        return ratingsMPA.size();
                    }
                });
    }
}
