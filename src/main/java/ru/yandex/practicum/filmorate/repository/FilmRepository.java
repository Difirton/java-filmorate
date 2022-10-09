package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.entity.Film;

import java.util.List;

public interface FilmRepository extends StandardCRUDRepository<Film> {

    List<Film> findPopularFilmsByRate(Integer count);

    List<Film> findPopularFilmsByRateWithGenreAndYear(Integer count, Integer genreID, Integer year);

    List<Film> findPopularFilmsByRateWithGenre(Integer count, Integer genreId);

    List<Film> findPopularFilmsByRateWithYear(Integer count, Integer year);

    int[][] updateAll(List<Film> films);

    List<Film> findFilmsByDirectorId(Long directorId);

    List<Film> findFilmsByDirectorId(Long directorId, String param);

    List<Film> findFilmsByIds(List<Long> filmIds);

    List<Film> searchFilmsByName(String query);

    List<Film> searchFilmsByDirectorName(String query);

    List<Film> findCommonFilms(Long userId, Long friendId);
}