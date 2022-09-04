package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.entity.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreRepository {

    Genre save(Genre genre);

    int update(Genre genre);

    int deleteById(Long id);

    List<Genre> findAll();

    Optional<Genre> findById(Long id);

    int[] saveAll(List<Genre> genres);

    List<Genre> findGenresByFilmId(Long filmId);
}
