package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.entity.Film;

import java.util.List;

public interface FilmRepository extends StandardCRUDRepository<Film> {

    List<Film> findPopularFilmsByRate(Integer count);

    int[][] updateAll(List<Film> films);
}