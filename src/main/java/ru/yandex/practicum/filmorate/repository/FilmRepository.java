package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.entity.Film;

import java.util.List;
import java.util.Optional;

public interface FilmRepository {

    Film save(Film film);

    Film update(Film film);

    int deleteById(Long id);

    List<Film> findAll();

    Optional<Film> findById(Long id);

    List<Film> findPopularFilmsByRate(Integer count);

    int[] saveAll(List<Film> film1);

    int[][] updateAll(List<Film> films);
}