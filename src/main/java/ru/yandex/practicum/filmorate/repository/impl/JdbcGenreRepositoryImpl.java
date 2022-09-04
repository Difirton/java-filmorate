package ru.yandex.practicum.filmorate.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
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
    private final JdbcTemplate jdbcTemplate;
    private final GenreRepositoryMapper genreMapper;

    @Autowired
    public JdbcGenreRepositoryImpl(JdbcTemplate jdbcTemplate, GenreRepositoryMapper genreMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreMapper = genreMapper;
    }

    @Override
    public Genre save(Genre genre) {
        this.jdbcTemplate.update("INSERT INTO genres (title) VALUES (?)",
                genre.getTitle());
        return genre;
    }

    @Override
    public int update(Genre genre) {
        return this.jdbcTemplate.update("UPDATE genres SET title = ? WHERE id = ?", genre.getTitle(), genre.getId());
    }

    @Override
    public int deleteById(Long id) {
        return this.jdbcTemplate.update("DELETE FROM genres WHERE id = ?", id);
    }

    @Override
    public List<Genre> findAll() {
        return this.jdbcTemplate.query("SELECT * FROM genres ORDER BY id", genreMapper);
    }

    @Override
    public Optional<Genre> findById(Long id) {
        return Optional.ofNullable(this.jdbcTemplate.queryForObject(
                "SELECT * FROM genres WHERE id = ?", genreMapper, id));
    }

    @Override
    public int[] saveAll(List<Genre> genres) {
        return this.jdbcTemplate.batchUpdate("INSERT INTO genres (title) VALUES (?)",
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
        return this.jdbcTemplate.query("SELECT G.id, G.title FROM genres AS G " +
                "INNER JOIN film_genres AS FG ON G.id = FG.genre_id WHERE FG.film_id = ?", genreMapper, filmId);
    }
}
