package ru.yandex.practicum.filmorate.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.config.mapper.*;
import ru.yandex.practicum.filmorate.entity.*;
import ru.yandex.practicum.filmorate.entity.binding.DirectorFilm;
import ru.yandex.practicum.filmorate.repository.FilmRepository;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FilmRepositoryJdbcImpl implements FilmRepository {
    private final JdbcOperations jdbcOperations;
    private final FilmRepositoryEagerMapper eagerFilmMapper;
    private final FilmRepositoryLazyMapper lazyFilmMapper;
    private final NamedParameterJdbcOperations namedJdbcTemplate;
    private final GenreRepositoryMapper genreMapper;
    private final DirectorRepositoryMapper directorMapper;
    private final UserFilmMarkMapper userFilmMarkMapper;

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
            "LEFT JOIN rating_mpa rm ON films.rating_mpa_id = rm.id WHERE films.id = ?";
    private static final String SQL_SELECT_POPULAR_FILMS = "SELECT * FROM films ORDER BY rate DESC LIMIT ?";
    private static final String SQL_INSERT_USERS_MARKS = "INSERT INTO users_films_marks (film_id, user_id, mark) VALUES (?, ?, ?)";
    private static final String SQL_DELETE_USERS_MARKS = "DELETE FROM users_films_marks " +
            "WHERE film_id = ? AND user_id = ? AND mark = ?";
    private static final String SQL_SELECT_ALL_USERS_MARKS = "SELECT ufm.id, ufm.user_id, u.name, u.email, u.login, " +
            "u.birthday, ufm.film_id, f.name, f.description, f.duration, f.release_date, f.rate, ufm.mark  " +
            "FROM users_films_marks AS ufm INNER JOIN users AS u ON ufm.user_id = u.id " +
            "INNER JOIN films AS f ON ufm.film_id = f.id WHERE film_id = ?";
    private static final String SQL_INSERT_DIRECTORS_FILMS = "INSERT INTO directors_films (film_id, director_id) " +
            "VALUES (?,?)";
    private static final String SQL_DELETE_DIRECTORS_FILMS = "DELETE FROM directors_films " +
            "WHERE film_id = ? AND director_id = ?";
    private static final String SQL_SELECT_DIRECTORS_FILMS = "SELECT director_id FROM directors_films WHERE film_id = ?";
    private static final String SQL_SELECT_ALL_DIRECTORS_FILM = "SELECT id, name FROM directors_films AS df " +
            "INNER JOIN directors as d ON d.id = df.director_id WHERE film_id = ?";
    private static final String SQL_SELECT_ALL_GENRES_FILM = "SELECT id, title FROM genres AS g " +
            "INNER JOIN film_genres AS fg ON fg.genre_id = g.id WHERE film_id = ?";
    private static final String SQL_SELECT_FILMS_BY_DIRECTOR_ID = "SELECT * FROM films " +
            "INNER JOIN directors_films df ON films.id = df.film_id WHERE df.director_id = ? ";
    private static final String NAMED_SQL_SELECT_FILMS_WITH_IDS = "SELECT * FROM films WHERE id IN (:ids)";
    private static final String SQL_SELECT_COMMON_FILMS = "SELECT * FROM films LEFT JOIN rating_mpa mpa " +
            "ON films.rating_mpa_id = mpa.id WHERE films.id IN " +
            "(SELECT user_likes.film_id FROM users_films_marks user_likes INNER JOIN users_films_marks friend_likes " +
            "ON user_likes.film_id = friend_likes.film_id WHERE user_likes.user_id = ? AND friend_likes.user_id = ? " +
            "AND user_likes.mark > 5 AND friend_likes.mark > 5) ORDER BY rate DESC";
    private static final String SQL_SELECT_POPULAR_FILMS_WITH_GENRE = "SELECT films.* FROM films " +
            "INNER JOIN film_genres AS fg ON fg.film_id = films.id " +
            "WHERE fg.genre_id = ?1 ORDER BY rate DESC LIMIT ?2";
    private static final String SQL_SELECT_POPULAR_FILMS_WITH_YEAR = "SELECT * FROM films " +
            "WHERE YEAR(release_date) = ?1 ORDER BY rate DESC LIMIT ?2";
    private static final String SQL_SELECT_POPULAR_FILMS_WITH_GENRE_AND_YEAR = "SELECT films.* FROM films " +
            "INNER JOIN film_genres AS fg ON fg.film_id = films.id " +
            "WHERE fg.genre_id = ?1 AND YEAR(release_date) = ?2 ORDER BY rate DESC LIMIT ?3";

    private static final String SQL_SELECT_FILMS_LIKE_NAME_WITH_ORDER_RATE = "SELECT * FROM films WHERE name " +
            "ILIKE :query ORDER BY rate DESC";
    private static final String SQL_SELECT_FILMS_LIKE_DIRECTOR_NAME_WITH_ORDER_RATE = "SELECT * FROM films WHERE id IN " +
            "(SELECT df.film_id " +
            "FROM directors_films df " +
            "INNER JOIN directors d ON d.id = df.director_id " +
            "WHERE d.name ILIKE :query)" +
            "ORDER BY rate DESC";

    @Override
    public Film save(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        this.jdbcOperations.update(this.createPreparedStatement(film), keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        List<Long> batchGenresIdToInsert = film.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toList());
        this.updateFilmCollection(film.getId(), batchGenresIdToInsert, SQL_INSERT_FILM_GENRES);
        List<Long> batchDirectorsIdToInsert = film.getDirectors().stream()
                .map(Director::getId)
                .collect(Collectors.toList());
        this.updateFilmCollection(film.getId(), batchDirectorsIdToInsert, SQL_INSERT_DIRECTORS_FILMS);
        return film;
    }

    private PreparedStatementCreator createPreparedStatement(Film film) {
        return connection -> {
            PreparedStatement ps = connection.prepareStatement(SQL_INSERT_FILMS_ALL_ARGS,
                    Statement.RETURN_GENERATED_KEYS);
            this.mapFilmInStatement(film, ps);
            return ps;
        };
    }

    private void mapFilmInStatement(Film film, PreparedStatement ps) throws SQLException {
        ps.setString(1, film.getName());
        ps.setString(2, film.getDescription());
        ps.setDate(3, Date.valueOf(film.getReleaseDate()));
        ps.setInt(4, film.getDuration());
        ps.setDouble(5, film.getRate());
        ps.setLong(6, film.getRatingMPA().getId());
    }

    @Override
    public Film update(Film film) {
        this.jdbcOperations.update(SQL_UPDATE_FILMS_ALL_ARGS, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getRate(), film.getRatingMPA().getId(), film.getId());
        this.checkFilmGenre(film);
        this.checkFilmMarks(film);
        this.checkFilmDirectors(film);
        return film;
    }

    private void checkFilmGenre(Film film) {
        List<Long> genresIdBeforeUpdate = this.jdbcOperations.query(SQL_SELECT_GENRES_ID_BY_FILM_ID,
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
        this.updateFilmCollection(film.getId(), genresIdToInsert, SQL_INSERT_FILM_GENRES);
        this.updateFilmCollection(film.getId(), genresIdToDelete, SQL_DELETE_FILM_GENRES);
    }

    private void checkFilmDirectors(Film film) {
        List<Long> usersIdBeforeUpdate = this.jdbcOperations.query(SQL_SELECT_DIRECTORS_FILMS,
                (rs, rowNum) -> rs.getLong("director_id"), film.getId());
        List<Long> usersIdAfterUpdate = film.getDirectors().stream()
                .map(Director::getId)
                .collect(Collectors.toList());
        List<Long> usersIdToDelete = usersIdBeforeUpdate.stream()
                .filter(id -> !usersIdAfterUpdate.contains(id))
                .collect(Collectors.toList());
        List<Long> usersIdToInsert = usersIdAfterUpdate.stream()
                .filter(id -> !usersIdBeforeUpdate.contains(id))
                .collect(Collectors.toList());
        this.updateFilmCollection(film.getId(), usersIdToInsert, SQL_INSERT_DIRECTORS_FILMS);
        this.updateFilmCollection(film.getId(), usersIdToDelete, SQL_DELETE_DIRECTORS_FILMS);
    }


    private void updateFilmCollection(Long filmId, List<Long> elementsCollectionId, String sqlRequiredOperation) {
        if (!elementsCollectionId.isEmpty()) {
            this.jdbcOperations.batchUpdate(sqlRequiredOperation,
                    new BatchPreparedStatementSetter() {
                        public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                            preparedStatement.setLong(1, filmId);
                            preparedStatement.setLong(2, elementsCollectionId.get(i));
                        }

                        public int getBatchSize() {
                            return elementsCollectionId.size();
                        }
                    });
        }
    }

    private void checkFilmMarks(Film film) {
        List<UserFilmMark> usersMarksBeforeUpdate = this.jdbcOperations.query(SQL_SELECT_ALL_USERS_MARKS,
                userFilmMarkMapper, film.getId());
        List<UserFilmMark> usersMarksAfterUpdate = film.getUsersMarks();
        List<UserFilmMark> usersMarksDelete = usersMarksBeforeUpdate.stream()
                .filter(fm -> !usersMarksAfterUpdate.contains(fm))
                .collect(Collectors.toList());
        List<UserFilmMark> usersMarksInsert = usersMarksAfterUpdate.stream()
                .filter(fm -> !usersMarksBeforeUpdate.contains(fm))
                .collect(Collectors.toList());
        this.updateFilmMarks(film.getId(), usersMarksInsert, SQL_INSERT_USERS_MARKS);
        this.updateFilmMarks(film.getId(), usersMarksDelete, SQL_DELETE_USERS_MARKS);
    }

    private void updateFilmMarks(Long filmId, List<UserFilmMark> userFilmMarks, String sqlRequiredOperation) {
        if (!userFilmMarks.isEmpty()) {
            this.jdbcOperations.batchUpdate(sqlRequiredOperation,
                    new BatchPreparedStatementSetter() {
                        public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                            preparedStatement.setLong(1, filmId);
                            preparedStatement.setLong(2, userFilmMarks.get(i).getUser().getId());
                            preparedStatement.setLong(3, userFilmMarks.get(i).getMark());
                        }

                        public int getBatchSize() {
                            return userFilmMarks.size();
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
    public Optional<Film> findById(Long filmId) {
        Film film = this.jdbcOperations.queryForObject(SQL_SELECT_ALL_FILMS_WITH_RATING,
                eagerFilmMapper, filmId);
        if (film != null) {
            film.setUsersMarks(this.jdbcOperations.query(SQL_SELECT_ALL_USERS_MARKS, userFilmMarkMapper, filmId));
            film.setDirectors(this.jdbcOperations.query(SQL_SELECT_ALL_DIRECTORS_FILM, directorMapper, filmId));
            film.setGenres(this.jdbcOperations.query(SQL_SELECT_ALL_GENRES_FILM, genreMapper, filmId));
            return Optional.of(film);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public int[] saveAll(List<Film> films) {
        int[] result = this.jdbcOperations.batchUpdate(SQL_INSERT_FILMS_ALL_ARGS,
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, films.get(i).getName());
                        ps.setString(2, films.get(i).getDescription());
                        ps.setDate(3, Date.valueOf(films.get(i).getReleaseDate()));
                        ps.setInt(4, films.get(i).getDuration());
                        ps.setDouble(5, films.get(i).getRate());
                        ps.setLong(6, films.get(i).getRatingMPA().getId());
                    }

                    public int getBatchSize() {
                        return films.size();
                    }
                });
        this.insertDirectorsFilms(films);
        return result;
    }

    private void insertDirectorsFilms(List<Film> films) {
        List<DirectorFilm> directorsFilms = films.stream()
                .map(f -> f.getDirectors().stream()
                        .map(o -> DirectorFilm.builder()
                                .filmId(f.getId())
                                .directorId(o.getId())
                                .build())
                        .collect(Collectors.toList()))
                .flatMap(List::stream)
                .collect(Collectors.toList());
        this.jdbcOperations.batchUpdate(SQL_INSERT_DIRECTORS_FILMS,
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, directorsFilms.get(i).getFilmId());
                        ps.setLong(2, directorsFilms.get(i).getDirectorId());
                    }

                    public int getBatchSize() {
                        return directorsFilms.size();
                    }
                });
    }

    @Override
    public List<Film> findPopularFilmsByRate(Integer count) {
        return this.jdbcOperations.query(SQL_SELECT_POPULAR_FILMS, lazyFilmMapper, count);
    }

    @Override
    public List<Film> findPopularFilmsByRateWithGenre(Integer count, Integer genreId) {
        return this.jdbcOperations.query(SQL_SELECT_POPULAR_FILMS_WITH_GENRE, lazyFilmMapper, genreId, count);
    }

    @Override
    public List<Film> findPopularFilmsByRateWithYear(Integer count, Integer year) {
        return this.jdbcOperations.query(SQL_SELECT_POPULAR_FILMS_WITH_YEAR, lazyFilmMapper, year, count);
    }

    @Override
    public List<Film> findPopularFilmsByRateWithGenreAndYear(Integer count, Integer genreId, Integer year) {
        return this.jdbcOperations.query(SQL_SELECT_POPULAR_FILMS_WITH_GENRE_AND_YEAR, lazyFilmMapper, genreId, year, count);
    }

    @Override
    public int[][] updateAll(List<Film> films) {
        return jdbcOperations.batchUpdate(SQL_UPDATE_FILMS_ALL_ARGS,
                films, films.size(),
                (ps, film) -> {
                    mapFilmInStatement(film, ps);
                    ps.setLong(7, film.getId());
                });
    }

    @Override
    public List<Film> findFilmsByDirectorId(Long directorId) {
        return this.jdbcOperations.query(SQL_SELECT_FILMS_BY_DIRECTOR_ID, lazyFilmMapper, directorId);
    }

    @Override
    public List<Film> findFilmsByDirectorId(Long directorId, String param) {
        String sqlResultParametriseQuery;
        switch (param) {
            case "year":
                sqlResultParametriseQuery = SQL_SELECT_FILMS_BY_DIRECTOR_ID + "ORDER BY release_date";
                return this.jdbcOperations.query(sqlResultParametriseQuery, lazyFilmMapper, directorId);
            case "likes":
                sqlResultParametriseQuery = SQL_SELECT_FILMS_BY_DIRECTOR_ID + "ORDER BY rate DESC";
                return this.jdbcOperations.query(sqlResultParametriseQuery, lazyFilmMapper, directorId);
            default:
                throw new IllegalStateException("Invalid request parameter passed: " + param);
        }
    }

    @Override
    public List<Film> findFilmsByIds(List<Long> filmsIds) {
        SqlParameterSource parameters = new MapSqlParameterSource("ids", filmsIds);
        return namedJdbcTemplate.query(NAMED_SQL_SELECT_FILMS_WITH_IDS, parameters, lazyFilmMapper);
    }

    @Override
    public List<Film> searchFilmsByName(String query) {
        SqlParameterSource parameters = new MapSqlParameterSource("query", "%" + query + "%");
        return namedJdbcTemplate.query(SQL_SELECT_FILMS_LIKE_NAME_WITH_ORDER_RATE, parameters, lazyFilmMapper);
    }

    @Override
    public List<Film> searchFilmsByDirectorName(String query) {
        SqlParameterSource parameters = new MapSqlParameterSource("query", "%" + query + "%");
        return namedJdbcTemplate.query(SQL_SELECT_FILMS_LIKE_DIRECTOR_NAME_WITH_ORDER_RATE, parameters, lazyFilmMapper);
    }
    
    @Override
    public List<Film> findCommonFilms(Long userId, Long friendId) {
        return this.jdbcOperations.query(SQL_SELECT_COMMON_FILMS, eagerFilmMapper, userId, friendId);
    }
}
