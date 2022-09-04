package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.entity.Genre;

import java.util.List;

public interface GenreRepository extends StandardCRUDRepository<Genre> {

    List<Genre> findGenresByFilmId(Long filmId);
}
