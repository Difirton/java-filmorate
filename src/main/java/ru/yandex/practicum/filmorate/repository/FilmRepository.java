package ru.yandex.practicum.filmorate.repository;

import org.springframework.expression.spel.ast.OpAnd;
import ru.yandex.practicum.filmorate.entity.Film;

import java.util.List;
import java.util.Optional;

public interface FilmRepository extends StandardCRUDRepository<Film> {

    List<Film> findPopularFilmsByRate(Integer count);

    List<Film> findPopularFilmsByRate(Integer count, Integer genreID, Integer year);

    int[][] updateAll(List<Film> films);

    List<Film> findFilmsByDirectorId(Long directorId);

    List<Film> findFilmsByDirectorId(Long directorId, String param);

    List<Film> findFilmsByIds(List<Long> filmIds);
}