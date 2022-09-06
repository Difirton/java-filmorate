package ru.yandex.practicum.filmorate.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
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

    @Autowired
    public JdbcFilmRepositoryImpl(JdbcOperations jdbcOperations, FilmRepositoryEagerMapper eagerFilmMapper,
                                  FilmRepositoryLazyMapper lazyFilmMapper) {
        this.jdbcOperations = jdbcOperations;
        this.eagerFilmMapper = eagerFilmMapper;
        this.lazyFilmMapper = lazyFilmMapper;
    }

    @Override
    public Film save(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        this.jdbcOperations.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(
                            "INSERT INTO films (name, description, release_date, duration, rate, rating_mpa_id) VALUES (?,?,?,?,?,?)",
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
        this.insertFilmGenres(film.getId(), batchIdToInsert);
        return film;
    }

    private int[] insertFilmGenres(Long filmId, List<Long> genresId) {
        if (!genresId.isEmpty()) {
            return this.jdbcOperations.batchUpdate(
                    "INSERT INTO film_genres (film_id, genre_id) VALUES (?,?)",
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
        else return new int[]{0};
    }

    @Override
    @Transactional
    public Film update(Film film) {
        this.jdbcOperations.update(
                "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rate = ?, rating_mpa_id = ? WHERE id = ?",
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getRate(), film.getRatingMPA().getId(),
                film.getId());
        List<Long> genresIdBeforeUpdate =  this.jdbcOperations.query("SELECT genre_id FROM film_genres WHERE film_id = ?",
                (rs, rowNum) -> rs.getLong("genre_id"), film.getId());
        List<Long> genresIdAfterUpdate = film.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toList());
        List<Long> batchIdToDelete = genresIdBeforeUpdate.stream()
                .filter(id -> !genresIdAfterUpdate.contains(id))
                .collect(Collectors.toList());
        List<Long> batchIdToInsert = genresIdAfterUpdate.stream()
                .filter(id -> !genresIdBeforeUpdate.contains(id))
                .collect(Collectors.toList());
        this.insertFilmGenres(film.getId(), batchIdToInsert);
        this.deleteFilmGenres(film.getId(), batchIdToDelete);
        return film;
    }

    private void deleteFilmGenres(Long filmId, List<Long> genresId) {
        if (!genresId.isEmpty()) {
            this.jdbcOperations.batchUpdate(
                    "DELETE FROM film_genres WHERE film_id = ? AND genre_id = ?",
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
        return this.jdbcOperations.update("DELETE FROM films WHERE id = ?", id);
    }

    @Override
    public List<Film> findAll() {
        return this.jdbcOperations.query("SELECT * FROM films", lazyFilmMapper);
    }

    @Override
    public Optional<Film> findById(Long id) {
        return Optional.ofNullable(this.jdbcOperations.queryForObject(
                "SELECT * FROM films " +
                        "LEFT JOIN RATING_MPA RM ON FILMS.RATING_MPA_ID = RM.ID WHERE FILMS.ID = ?", eagerFilmMapper, id));
    }

    @Override
    public List<Film> findPopularFilmsByRate(Integer count) {
        return this.jdbcOperations.query(
                "SELECT * FROM films  ORDER BY rate DESC LIMIT ?", lazyFilmMapper, count);
    }

    @Override
    @Transactional
    public int[] saveAll(List<Film> films) {
        return this.jdbcOperations.batchUpdate(
                "INSERT INTO films (name, description, release_date, duration, rate, rating_mpa_id) VALUES (?,?,?,?,?,?)",
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
        return jdbcOperations.batchUpdate(
                "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rate = ? WHERE id = ?",
                films, films.size(),
                (preparedStatement, film) -> {
                    preparedStatement.setString(1, film.getName());
                    preparedStatement.setString(2, film.getDescription());
                    preparedStatement.setDate(3, Date.valueOf(film.getReleaseDate()));
                    preparedStatement.setInt(4, film.getDuration());
                    preparedStatement.setInt(5, film.getRate());
                    preparedStatement.setLong(6, film.getId());
                });
    }
}
