package ru.yandex.practicum.filmorate.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.entity.binding.FilmGenre;
import ru.yandex.practicum.filmorate.repository.FilmGenreRepository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FilmGenreRepositoryJdbcImpl implements FilmGenreRepository {
    private final JdbcOperations jdbcOperations;
    private final String SQL_SELECT_ALL = "SELECT * FROM film_genres";

    @Override
    public List<FilmGenre> findAll() {
        return jdbcOperations.query(SQL_SELECT_ALL,  (rs, rowNum) -> FilmGenre.builder()
                .filmId(rs.getLong("film_id"))
                .genreId(rs.getLong("genre_id"))
                .build());
    }
}
