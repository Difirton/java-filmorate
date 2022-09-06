package ru.yandex.practicum.filmorate.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.config.mapper.GenreRepositoryMapper;
import ru.yandex.practicum.filmorate.entity.Genre;
import ru.yandex.practicum.filmorate.repository.GenreRepository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcGenreRepositoryImpl implements GenreRepository {
    private final JdbcOperations jdbcOperations;
    private final GenreRepositoryMapper genreMapper;

    @Autowired
    public JdbcGenreRepositoryImpl(JdbcOperations jdbcOperations, GenreRepositoryMapper genreMapper) {
        this.jdbcOperations = jdbcOperations;
        this.genreMapper = genreMapper;
    }

    @Override
    public Genre save(Genre genre) {
        this.jdbcOperations.update("INSERT INTO genres (title) VALUES (?)",
                genre.getTitle());
        return genre;
    }

    @Override
    public Genre update(Genre genre) {
        this.jdbcOperations.update("UPDATE genres SET title = ? WHERE id = ?", genre.getTitle(), genre.getId());
        return genre;
    }

    @Override
    public int deleteById(Long id) {
        return this.jdbcOperations.update("DELETE FROM genres WHERE id = ?", id);
    }

    @Override
    public List<Genre> findAll() {
        return this.jdbcOperations.query("SELECT * FROM genres ORDER BY id", genreMapper);
    }

    @Override
    public Optional<Genre> findById(Long id) {
        return Optional.ofNullable(this.jdbcOperations.queryForObject(
                "SELECT * FROM genres WHERE id = ?", genreMapper, id));
    }

    @Override
    public int[] saveAll(List<Genre> genres) {
        return this.jdbcOperations.batchUpdate("INSERT INTO genres (title) VALUES (?)",
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                        preparedStatement.setString(1, genres.get(i).getTitle());
                    }
                    public int getBatchSize() {
                        return genres.size();
                    }
                });
    }

    @Override
    public List<Genre> findGenresByFilmId(Long filmId) {
        return this.jdbcOperations.query("SELECT G.id, G.title FROM genres AS G " +
                "INNER JOIN film_genres AS FG ON G.id = FG.genre_id WHERE FG.film_id = ?", genreMapper, filmId);
    }
}
