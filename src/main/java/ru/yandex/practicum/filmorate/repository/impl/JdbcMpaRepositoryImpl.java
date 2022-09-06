package ru.yandex.practicum.filmorate.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.entity.RatingMPA;
import ru.yandex.practicum.filmorate.repository.RatingMpaRepository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcMpaRepositoryImpl implements RatingMpaRepository {
    private final JdbcOperations jdbcOperations;

    @Autowired
    public JdbcMpaRepositoryImpl(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public RatingMPA save(RatingMPA ratingMPA) {
        this.jdbcOperations.update("INSERT INTO rating_mpa (title) VALUES (?)",
                ratingMPA.getTitle());
        return ratingMPA;
    }

    @Override
    public RatingMPA update(RatingMPA ratingMPA) {
        this.jdbcOperations.update("UPDATE rating_mpa SET title = ? WHERE id = ?",
                ratingMPA.getTitle(), ratingMPA.getId());
        return ratingMPA;
    }

    @Override
    public int deleteById(Long id) {
        return jdbcOperations.update("DELETE FROM rating_mpa WHERE id = ?", id);
    }

    @Override
    public List<RatingMPA> findAll() {
        return this.jdbcOperations.query("SELECT * FROM rating_mpa ORDER BY id",
                (resultSet, rowNum) -> RatingMPA.builder()
                        .id(resultSet.getLong("id"))
                        .title(resultSet.getString("title")).build());
    }

    @Override
    public Optional<RatingMPA> findById(Long id) {
        return this.jdbcOperations.queryForObject(
                "SELECT * FROM rating_mpa WHERE id = ?",
                (resultSet, rowNum) -> Optional.of(RatingMPA.builder()
                        .id(resultSet.getLong("id"))
                        .title(resultSet.getString("title")).build()), id);
    }

    @Override
    public int[] saveAll(List<RatingMPA> ratingsMPA) {
        return this.jdbcOperations.batchUpdate("INSERT INTO rating_mpa (title) VALUES (?)",
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
