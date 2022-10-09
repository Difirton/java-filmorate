package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.entity.binding.DirectorFilm;

import java.util.List;

public interface DirectorFilmRepository {
    List<DirectorFilm> findAll();
}
