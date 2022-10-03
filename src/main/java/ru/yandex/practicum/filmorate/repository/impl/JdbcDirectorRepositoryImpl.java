package ru.yandex.practicum.filmorate.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.config.mapper.DirectorRepositoryMapper;
import ru.yandex.practicum.filmorate.entity.Director;
import ru.yandex.practicum.filmorate.repository.DirectorRepository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcDirectorRepositoryImpl implements DirectorRepository {
    private final JdbcOperations jdbcOperations;
    private final DirectorRepositoryMapper directorMapper;
    private final String SQL_INSERT_NAME = "INSERT INTO directors (name) VALUES (?)";
    private final String SQL_UPDATE_NAME_BY_ID = "UPDATE directors SET name = ? WHERE id = ?";
    private final String SQL_DELETE_BY_ID = "DELETE FROM directors WHERE id = ?";
    private final String SQL_SELECT_ALL = "SELECT * FROM directors ORDER BY id";
    private final String SQL_SELECT_BY_ID = "SELECT * FROM directors WHERE id = ?";

    @Override
    public Director save(Director director) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        this.jdbcOperations.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(SQL_INSERT_NAME, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);
        director.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return director;
    }

    @Override
    public Director update(Director director) {
        this.jdbcOperations.update(SQL_UPDATE_NAME_BY_ID, director.getName(), director.getId());
        return director;
    }

    @Override
    public int deleteById(Long id) {
        return this.jdbcOperations.update(SQL_DELETE_BY_ID, id);
    }

    @Override
    public List<Director> findAll() {
        return this.jdbcOperations.query(SQL_SELECT_ALL, directorMapper);
    }

    @Override
    public Optional<Director> findById(Long id) {
        return Optional.ofNullable(this.jdbcOperations.queryForObject(SQL_SELECT_BY_ID, directorMapper, id));
    }

    @Override
    public int[] saveAll(List<Director> directors) {
        return this.jdbcOperations.batchUpdate(SQL_INSERT_NAME,
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                        preparedStatement.setString(1, directors.get(i).getName());
                    }
                    public int getBatchSize() {
                        return directors.size();
                    }
                });
    }
}
