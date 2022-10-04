package ru.yandex.practicum.filmorate.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.entity.binding.FilmGenre;
import ru.yandex.practicum.filmorate.repository.FilmGenreRepository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FilmGenreRepositoryJdbcImpl implements FilmGenreRepository {
    private final JdbcOperations jdbcOperations;
    private final NamedParameterJdbcOperations namedJdbcTemplate;
    private final String SQL_SELECT_ALL = "SELECT film_id, genre_id FROM film_genres";
    private static final String NAMED_SQL_SELECT_FILMS_GENRES_BY_FILMS_IDS = "SELECT film_id, genre_id" +
            " FROM film_genres WHERE film_id IN (:ids)";
    private static final String SQL_INSERT_FILM_ID_GENRE_ID = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";

    @Override
    public List<FilmGenre> findAll() {
        return jdbcOperations.query(SQL_SELECT_ALL,  (rs, rowNum) -> FilmGenre.builder()
                .filmId(rs.getLong("film_id"))
                .genreId(rs.getLong("genre_id"))
                .build());
    }

    @Override
    public List<FilmGenre> findFilmsGenresByFilmsIds(List<Long> filmsIds) {
        SqlParameterSource parameters = new MapSqlParameterSource("ids", filmsIds);
        return namedJdbcTemplate.query(NAMED_SQL_SELECT_FILMS_GENRES_BY_FILMS_IDS, parameters,
                (rs, rowNum) -> FilmGenre.builder()
                        .filmId(rs.getLong("film_id"))
                        .genreId(rs.getLong("genre_id"))
                        .build());
    }

    @Override
    public int[] saveFilmGenres(Long filmId, List<Long> genresIds) {
        return this.jdbcOperations.batchUpdate(SQL_INSERT_FILM_ID_GENRE_ID,
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                        preparedStatement.setLong(1, filmId);
                        preparedStatement.setLong(2, genresIds.get(i));
                    }
                    public int getBatchSize() {
                        return genresIds.size();
                    }
                });
    }
}
