package ru.yandex.practicum.filmorate.repository.impl;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class JdbcGenreRepositoryImpl implements GenreRepository {
    private final JdbcOperations jdbcOperations;
    private final GenreRepositoryMapper genreMapper;
    private static final String SQL_INSERT_TITLE = "INSERT INTO genres (title) VALUES (?)";
    private static final String SQL_UPDATE_TITLE_BY_ID = "UPDATE genres SET title = ? WHERE id = ?";
    private static final String SQL_DELETE_BY_ID = "DELETE FROM genres WHERE id = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM genres ORDER BY id";
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM genres WHERE id = ?";
    private static final String SQL_SELECT_GENRES_BY_FILM_ID = "SELECT G.id, G.title FROM genres AS G " +
            "INNER JOIN film_genres AS FG ON G.id = FG.genre_id WHERE FG.film_id = ?";

    @Override
    public Genre save(Genre genre) {
        this.jdbcOperations.update(SQL_INSERT_TITLE, genre.getTitle());
        return genre;
    }

    @Override
    public Genre update(Genre genre) {
        this.jdbcOperations.update(SQL_UPDATE_TITLE_BY_ID, genre.getTitle(), genre.getId());
        return genre;
    }

    @Override
    public int deleteById(Long id) {
        return this.jdbcOperations.update(SQL_DELETE_BY_ID, id);
    }

    @Override
    public List<Genre> findAll() {
        return this.jdbcOperations.query(SQL_SELECT_ALL, genreMapper);
    }

    @Override
    public Optional<Genre> findById(Long id) {
        return Optional.ofNullable(this.jdbcOperations.queryForObject(SQL_SELECT_BY_ID, genreMapper, id));
    }

    @Override
    public int[] saveAll(List<Genre> genres) {
        return this.jdbcOperations.batchUpdate(SQL_INSERT_TITLE,
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
        return this.jdbcOperations.query(SQL_SELECT_GENRES_BY_FILM_ID, genreMapper, filmId);
    }
}
