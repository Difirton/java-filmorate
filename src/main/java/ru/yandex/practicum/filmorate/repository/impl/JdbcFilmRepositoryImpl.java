package ru.yandex.practicum.filmorate.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.config.mapper.FilmRepositoryEagerMapper;
import ru.yandex.practicum.filmorate.config.mapper.FilmRepositoryLazyMapper;
import ru.yandex.practicum.filmorate.entity.Film;
import ru.yandex.practicum.filmorate.entity.Genre;
import ru.yandex.practicum.filmorate.repository.FilmRepository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class JdbcFilmRepositoryImpl implements FilmRepository {
    private final JdbcOperations jdbcOperations;
    private final FilmRepositoryEagerMapper eagerFilmMapper;
    private final FilmRepositoryLazyMapper lazyFilmMapper;
    private static final String SQL_INSERT_FILM_GENRES = "INSERT INTO film_genres (film_id, genre_id) VALUES (?,?)";
    private static final String SQL_DELETE_FILM_GENRES = "DELETE FROM film_genres WHERE film_id = ? AND genre_id = ?";
    private static final String SQL_INSERT_FILMS_ALL_ARGS = "INSERT INTO films (name, description, release_date, " +
            "duration, rate, rating_mpa_id) VALUES (?,?,?,?,?,?)";
    private static final String SQL_UPDATE_FILMS_ALL_ARGS = "UPDATE films SET name = ?, description = ?, " +
            "release_date = ?, duration = ?, rate = ?, rating_mpa_id = ? WHERE id = ?";
    private static final String SQL_SELECT_GENRES_ID_BY_FILM_ID = "SELECT genre_id FROM film_genres WHERE film_id = ?";
    private static final String SQL_DELETE_FILM_BY_ID = "DELETE FROM films WHERE id = ?";
    private static final String SQL_SELECT_ALL_FILMS_WITHOUT_RATING = "SELECT * FROM films";
    private static final String SQL_SELECT_ALL_FILMS_WITH_RATING = "SELECT * FROM films " +
            "LEFT JOIN RATING_MPA RM ON FILMS.RATING_MPA_ID = RM.ID WHERE FILMS.ID = ?";
    private static final String SQL_SELECT_POPULAR_FILMS = "SELECT * FROM films ORDER BY rate DESC LIMIT ?";

    @Autowired
    public JdbcFilmRepositoryImpl(JdbcOperations jdbcOperations, FilmRepositoryEagerMapper eagerFilmMapper,
                                  FilmRepositoryLazyMapper lazyFilmMapper) {
        this.jdbcOperations = jdbcOperations;
        this.eagerFilmMapper = eagerFilmMapper;
        this.lazyFilmMapper = lazyFilmMapper;
    }

    @Override
    @Transactional
    public Film save(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        this.jdbcOperations.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(SQL_INSERT_FILMS_ALL_ARGS,
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getRate());
            ps.setLong(6, film.getRatingMPA().getId());
            return ps;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        List<Long> batchIdToInsert = film.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toList());
        this.updateFilmGenres(film.getId(), batchIdToInsert, SQL_INSERT_FILM_GENRES);
        return film;
    }

    @Override
    @Transactional
    public Film update(Film film) {
        this.jdbcOperations.update(SQL_UPDATE_FILMS_ALL_ARGS, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getRate(), film.getRatingMPA().getId(), film.getId());
        List<Long> genresIdBeforeUpdate =  this.jdbcOperations.query(SQL_SELECT_GENRES_ID_BY_FILM_ID,
                (rs, rowNum) -> rs.getLong("genre_id"), film.getId());
        List<Long> genresIdAfterUpdate = film.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toList());
        List<Long> genresIdToDelete = genresIdBeforeUpdate.stream()
                .filter(id -> !genresIdAfterUpdate.contains(id))
                .collect(Collectors.toList());
        List<Long> genresIdToInsert = genresIdAfterUpdate.stream()
                .filter(id -> !genresIdBeforeUpdate.contains(id))
                .collect(Collectors.toList());
        this.updateFilmGenres(film.getId(), genresIdToInsert, SQL_INSERT_FILM_GENRES);
        this.updateFilmGenres(film.getId(), genresIdToDelete, SQL_DELETE_FILM_GENRES);
        return film;
    }

    private void updateFilmGenres(Long filmId, List<Long> genresId, String sqlRequiredOperation) {
        if (!genresId.isEmpty()) {
            this.jdbcOperations.batchUpdate(sqlRequiredOperation,
                    new BatchPreparedStatementSetter() {
                        public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                            preparedStatement.setLong(1, filmId);
                            preparedStatement.setLong(2, genresId.get(i));
                        }
                        public int getBatchSize() {
                            return genresId.size();
                        }
                    });
        }
    }

    @Override
    public int deleteById(Long id) {
        return this.jdbcOperations.update(SQL_DELETE_FILM_BY_ID, id);
    }

    @Override
    public List<Film> findAll() {
        return this.jdbcOperations.query(SQL_SELECT_ALL_FILMS_WITHOUT_RATING, lazyFilmMapper);
    }

    @Override
    public Optional<Film> findById(Long id) {
        return Optional.ofNullable(this.jdbcOperations.queryForObject(SQL_SELECT_ALL_FILMS_WITH_RATING,
                eagerFilmMapper, id));
    }

    @Override
    public List<Film> findPopularFilmsByRate(Integer count) {
        return this.jdbcOperations.query(SQL_SELECT_POPULAR_FILMS, lazyFilmMapper, count);
    }

    @Override
    @Transactional
    public int[] saveAll(List<Film> films) {
        return this.jdbcOperations.batchUpdate(SQL_INSERT_FILMS_ALL_ARGS,
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                        preparedStatement.setString(1, films.get(i).getName());
                        preparedStatement.setString(2, films.get(i).getDescription());
                        preparedStatement.setDate(3, Date.valueOf(films.get(i).getReleaseDate()));
                        preparedStatement.setInt(4, films.get(i).getDuration());
                        preparedStatement.setInt(5, films.get(i).getRate());
                        preparedStatement.setLong(6, films.get(i).getRatingMPA().getId());
                    }
                    public int getBatchSize() {
                        return films.size();
                    }
                });
    }

    @Override
    public int[][] updateAll(List<Film> films) {
        return jdbcOperations.batchUpdate(SQL_UPDATE_FILMS_ALL_ARGS,
                films, films.size(),
                (preparedStatement, film) -> {
                    preparedStatement.setString(1, film.getName());
                    preparedStatement.setString(2, film.getDescription());
                    preparedStatement.setDate(3, Date.valueOf(film.getReleaseDate()));
                    preparedStatement.setInt(4, film.getDuration());
                    preparedStatement.setInt(5, film.getRate());
                    preparedStatement.setLong(6, film.getRatingMPA().getId());
                    preparedStatement.setLong(7, film.getId());
                });
    }
}
