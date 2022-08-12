package ru.yandex.practicum.filmorate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.filmorate.entity.Film;

import java.util.List;

public interface FilmRepository extends JpaRepository<Film, Long> {

    @Query(value = "SELECT * FROM films WHERE id IN " +
            "(SELECT film_id FROM users_likes_films GROUP BY film_id ORDER BY COUNT(user_id) DESC)" +
            "LIMIT ?1", nativeQuery = true)
    List<Film> getPopularFilms(Integer count); //TODO нужны тесты f.id возможно надо заменить на id или film_id
}