package ru.yandex.practicum.filmorate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.filmorate.entity.Film;

import java.util.List;

public interface FilmRepository extends JpaRepository<Film, Long> {

    @Query(value = "SELECT * FROM films ORDER BY rate DESC LIMIT ?1", nativeQuery = true)
    List<Film> findPopularFilmsByRate(Integer count);
}