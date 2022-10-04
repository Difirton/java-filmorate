package ru.yandex.practicum.filmorate.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.entity.binding.DirectorFilm;
import ru.yandex.practicum.filmorate.repository.DirectorFilmRepository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DirectorFilmRepositoryJdbcImpl implements DirectorFilmRepository {
    private final JdbcOperations jdbcOperations;
    private final String SQL_SELECT_ALL = "SELECT * FROM directors_films";

    @Override
    public List<DirectorFilm> findAll() {
        return jdbcOperations.query(SQL_SELECT_ALL,  (rs, rowNum) -> DirectorFilm.builder()
                .directorId(rs.getLong("director_id"))
                .filmId(rs.getLong("film_id"))
                .build());
    }
}
