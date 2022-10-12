package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.entity.UserFilmMark;

import java.util.Optional;

public interface UserFilmMarkRepository extends StandardCRUDRepository<UserFilmMark> {

    Optional<UserFilmMark> findByUserIdAndFilmId(Long userId, Long filmId);
}
